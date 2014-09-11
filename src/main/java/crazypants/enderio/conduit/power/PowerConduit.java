package crazypants.enderio.conduit.power;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.monitor.PacketConduitProbe;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.render.BoundingBox;
import crazypants.render.IconUtil;
import crazypants.util.DyeColor;
import crazypants.vecmath.Vector3d;

public class PowerConduit extends AbstractConduit implements IPowerConduit {

  static final Map<String, IIcon> ICONS = new HashMap<String, IIcon>();

  static final ICapacitor[] CAPACITORS = new BasicCapacitor[] {
    new BasicCapacitor(500, 1500, 128),
    new BasicCapacitor(512, 3000, 512),
    new BasicCapacitor(2048, 5000, 2048)
  };

  static final String[] POSTFIX = new String[] { "", "Enhanced", "Ender" };

  static ItemStack createItemStackForSubtype(int subtype) {
    ItemStack result = new ItemStack(EnderIO.itemPowerConduit, 1, subtype);
    return result;
  }

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        for (String pf : POSTFIX) {
          ICONS.put(ICON_KEY + pf, register.registerIcon(ICON_KEY + pf));
          ICONS.put(ICON_KEY_INPUT + pf, register.registerIcon(ICON_KEY_INPUT + pf));
          ICONS.put(ICON_KEY_OUTPUT + pf, register.registerIcon(ICON_KEY_OUTPUT + pf));
          ICONS.put(ICON_CORE_KEY + pf, register.registerIcon(ICON_CORE_KEY + pf));
        }
        ICONS.put(ICON_TRANSMISSION_KEY, register.registerIcon(ICON_TRANSMISSION_KEY));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  public static final float WIDTH = 0.075f;
  public static final float HEIGHT = 0.075f;

  public static final Vector3d MIN = new Vector3d(0.5f - WIDTH, 0.5 - HEIGHT, 0.5 - WIDTH);
  public static final Vector3d MAX = new Vector3d(MIN.x + WIDTH, MIN.y + HEIGHT, MIN.z + WIDTH);

  public static final BoundingBox BOUNDS = new BoundingBox(MIN, MAX);

  protected PowerConduitNetwork network;

  private PowerHandler powerHandler;
  private PowerHandler noInputPH;

  private float energyStored;

  private int subtype;

  protected final EnumMap<ForgeDirection, RedstoneControlMode> rsModes = new EnumMap<ForgeDirection, RedstoneControlMode>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, DyeColor> rsColors = new EnumMap<ForgeDirection, DyeColor>(ForgeDirection.class);

  protected EnumMap<ForgeDirection, Long> recievedTicks;

  private final Map<ForgeDirection, Integer> externalRedstoneSignals = new HashMap<ForgeDirection, Integer>();

  private boolean redstoneStateDirty = true;

  public PowerConduit() {
  }

  public PowerConduit(int meta) {
    this.subtype = meta;
    //powerHandler = createPowerHandlerForType();
  }

  @Override
  public boolean getConnectionsDirty() {
    return connectionsDirty;
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    DyeColor col = DyeColor.getColorFromDye(player.getCurrentEquippedItem());
    if(ConduitUtil.isProbeEquipped(player)) {
      if(!player.worldObj.isRemote) {
        new PacketConduitProbe().sendInfoMessage(player, this);
      }
      return true;
    } else if(col != null && res.component != null && isColorBandRendered(res.component.dir)) {
      setExtractionSignalColor(res.component.dir, col);
      return true;
    } else if(ConduitUtil.isToolEquipped(player)) {
      if(!getBundle().getEntity().getWorldObj().isRemote) {
        if(res != null && res.component != null) {
          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);
          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
            if(getConectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }
            // Attempt to join networks
            return ConduitUtil.joinConduits(this, faceHit);
          } else if(externalConnections.contains(connDir)) {
            setConnectionMode(connDir, getNextConnectionMode(connDir));
            return true;
          } else if(containsConduitConnection(connDir)) {
            ConduitUtil.disconectConduits(this, connDir);
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean isColorBandRendered(ForgeDirection dir) {
    return getConectionMode(dir) != ConnectionMode.DISABLED && getExtractionRedstoneMode(dir) != RedstoneControlMode.IGNORE;
  }

  @Override
  public ICapacitor getCapacitor() {
    return CAPACITORS[subtype];
  }

  private PowerHandler createPowerHandlerForType() {
    return PowerHandlerUtil.createHandler(CAPACITORS[subtype], this, Type.PIPE);
  }

  @Override
  public void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir) {
    rsModes.put(dir, mode);
    setClientStateDirty();
  }

  @Override
  public RedstoneControlMode getExtractionRedstoneMode(ForgeDirection dir) {
    RedstoneControlMode res = rsModes.get(dir);
    if(res == null) {
      res = RedstoneControlMode.IGNORE;
    }
    return res;
  }

  @Override
  public void setExtractionSignalColor(ForgeDirection dir, DyeColor col) {
    rsColors.put(dir, col);
    setClientStateDirty();
  }

  @Override
  public DyeColor getExtractionSignalColor(ForgeDirection dir) {
    DyeColor res = rsColors.get(dir);
    if(res == null) {
      res = DyeColor.RED;
    }
    return res;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("subtype", (short) subtype);
    nbtRoot.setFloat("energyStored", energyStored);

    for (Entry<ForgeDirection, RedstoneControlMode> entry : rsModes.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("pRsMode." + entry.getKey().name(), ord);
      }
    }

    for (Entry<ForgeDirection, DyeColor> entry : rsColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("pRsCol." + entry.getKey().name(), ord);
      }
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);
    subtype = nbtRoot.getShort("subtype");

    energyStored = Math.min(getCapacitor().getMaxEnergyStored(), nbtRoot.getFloat("energyStored"));

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      String key = "pRsMode." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < RedstoneControlMode.values().length) {
          rsModes.put(dir, RedstoneControlMode.values()[ord]);
        }
      }
      key = "pRsCol." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < DyeColor.values().length) {
          rsColors.put(dir, DyeColor.values()[ord]);
        }
      }
    }
  }

  @Override
  public void onTick() {
    if(powerHandler != null && powerHandler.getEnergyStored() > 0) {
      energyStored = (float) Math.min(energyStored + powerHandler.getEnergyStored(), getCapacitor().getMaxEnergyStored());
      powerHandler.setEnergy(0);
    }
  }

  @Override
  public float getEnergyStored() {
    return energyStored;
  }

  @Override
  public void setEnergyStored(float energyStored) {
    this.energyStored = energyStored;
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    ConnectionMode mode = getConectionMode(side);
    if(side == null || mode == ConnectionMode.OUTPUT || mode == ConnectionMode.DISABLED || !isRedstoneEnabled(side)) {
      if(noInputPH == null) {
        noInputPH = new PowerHandler(this, Type.PIPE);
        noInputPH.configure(0, 0, 0, getOrCreatePowerHandler().getMaxEnergyStored());
      }
      return noInputPH.getPowerReceiver();
    }
    return getOrCreatePowerHandler().getPowerReceiver();
  }

  private PowerHandler getOrCreatePowerHandler() {
    if(powerHandler == null) {
      powerHandler = createPowerHandlerForType();
    }
    return powerHandler;
  }

  private boolean isRedstoneEnabled(ForgeDirection dir) {
    boolean result;
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    if(mode == RedstoneControlMode.NEVER) {
      return false;
    }
    if(mode == RedstoneControlMode.IGNORE) {
      return true;
    }

    DyeColor col = getExtractionSignalColor(dir);
    // internal signal
    int signal = ConduitUtil.getInternalSignalForColor(getBundle(), col);
    if(mode.isConditionMet(mode, signal)) {
      return true;
    }

    // external signal
    if(col != DyeColor.RED) {
      //can't get a non-red signal externally at this stage so no go
      return false;
    }
    int val = getExternalRedstoneSignalForDir(dir);
    return mode.isConditionMet(mode, val);
  }

  private int getExternalRedstoneSignalForDir(ForgeDirection dir) {
    if(redstoneStateDirty) {
      externalRedstoneSignals.clear();
      redstoneStateDirty = false;
    }
    Integer cached = externalRedstoneSignals.get(dir);
    int result;
    if(cached == null) {
      TileEntity te = getBundle().getEntity();
      result = te.getWorldObj().getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord);
      externalRedstoneSignals.put(dir, result);
    } else {
      result = cached;
    }
    return result;
  }

  @Override
  public float getMaxEnergyRecieved(ForgeDirection dir) {
    ConnectionMode mode = getConectionMode(dir);
    if(mode == ConnectionMode.OUTPUT || mode == ConnectionMode.DISABLED || !isRedstoneEnabled(dir)) {
      return 0;
    }
    return getCapacitor().getMaxEnergyReceived();
  }

  @Override
  public float getMaxEnergyExtracted(ForgeDirection dir) {
    ConnectionMode mode = getConectionMode(dir);
    if(mode == ConnectionMode.INPUT || mode == ConnectionMode.DISABLED || !isRedstoneEnabled(dir)) {
      return 0;
    }
    if(recievedRfThisTick(dir)) {
      return 0;
    }
    return getCapacitor().getMaxEnergyExtracted();
  }

  private boolean recievedRfThisTick(ForgeDirection dir) {
    if(recievedTicks == null || dir == null || recievedTicks.get(dir) == null || getBundle() == null || getBundle().getWorld() == null) {
      return false;
    }

    long curTick = getBundle().getWorld().getTotalWorldTime();
    long recT = recievedTicks.get(dir);
    if(curTick - recT <= 5) {
      return true;
    }
    return false;
  }

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public World getWorld() {
    return getBundle().getEntity().getWorldObj();
  }

  @Override
  public boolean onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
    if(network != null && network.powerManager != null) {
      network.powerManager.receptorsChanged();
    }
    return super.onNeighborBlockChange(blockId);
  }

  @Override
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
	  super.setConnectionMode(dir, mode);
	  recievedTicks = null;
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(getMaxEnergyRecieved(from) == 0 || maxReceive <= 0) {
      return 0;
    }
    float freeSpace = getCapacitor().getMaxEnergyStored() - energyStored;
    int result = (int) Math.min(maxReceive / 10, freeSpace);
    if(!simulate && result > 0) {
      energyStored += result;

      if(getBundle() != null) {
        if(recievedTicks == null) {
          recievedTicks = new EnumMap<ForgeDirection, Long>(ForgeDirection.class);
        }
        recievedTicks.put(from, getBundle().getWorld().getTotalWorldTime());
      }

    }
    return result * 10;
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return true;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int) (energyStored * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return getCapacitor().getMaxEnergyStored() * 10;
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    this.network = (PowerConduitNetwork) network;
    return true;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
    IPowerInterface rec = getExternalPowerReceptor(direction);
    return rec != null && rec.canConduitConnect(direction);
  }

  @Override
  public void externalConnectionAdded(ForgeDirection direction) {
    super.externalConnectionAdded(direction);
    if(network != null) {
      TileEntity te = bundle.getEntity();
      network.powerReceptorAdded(this, direction, te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ,
          getExternalPowerReceptor(direction));
    }
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection direction) {
    super.externalConnectionRemoved(direction);
    if(network != null) {
      TileEntity te = bundle.getEntity();
      network.powerReceptorRemoved(te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    }
  }

  @Override
  public IPowerInterface getExternalPowerReceptor(ForgeDirection direction) {
    TileEntity te = bundle.getEntity();
    World world = te.getWorldObj();
    if(world == null) {
      return null;
    }
    TileEntity test = world.getTileEntity(te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    if(test == null) {
      return null;
    }
    if(test instanceof IConduitBundle) {
      return null;
    }
    return PowerHandlerUtil.create(test);
  }

  @Override
  public ItemStack createItem() {
    return createItemStackForSubtype(subtype);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  // Rendering
  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY + POSTFIX[subtype]);
    }
    if(COLOR_CONTROLLER_ID.equals(component.data)) {
      return IconUtil.whiteTexture;
    }
    return ICONS.get(ICON_KEY + POSTFIX[subtype]);
  }

  @Override
  public IIcon getTextureForInputMode() {
    return ICONS.get(ICON_KEY_INPUT + POSTFIX[subtype]);
  }

  @Override
  public IIcon getTextureForOutputMode() {
    return ICONS.get(ICON_KEY_OUTPUT + POSTFIX[subtype]);
  }

  @Override
  public IIcon getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  public Collection<CollidableComponent> createCollidables(CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if(key.dir == ForgeDirection.UNKNOWN) {
      return baseCollidables;
    }

    BoundingBox bb = ConduitGeometryUtil.instance.createBoundsForConnectionController(key.dir, key.offset);
    CollidableComponent cc = new CollidableComponent(IPowerConduit.class, bb, key.dir, COLOR_CONTROLLER_ID);

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);
    result.add(cc);

    return result;
  }

  @Override
  public double getMaxEnergyStored() {
    return CAPACITORS[subtype].getMaxEnergyStored();
  }

}
