package crazypants.enderio.conduit.redstone;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;
import crazypants.util.DyeColor;

public class RedstoneConduit extends AbstractConduit implements IRedstoneConduit {

  static final Map<String, IIcon> ICONS = new HashMap<String, IIcon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        ICONS.put(KEY_CORE_OFF_ICON, register.registerIcon(KEY_CORE_OFF_ICON));
        ICONS.put(KEY_CORE_ON_ICON, register.registerIcon(KEY_CORE_ON_ICON));
        ICONS.put(KEY_CONDUIT_ICON, register.registerIcon(KEY_CONDUIT_ICON));
        ICONS.put(KEY_TRANSMISSION_ICON, register.registerIcon(KEY_TRANSMISSION_ICON));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  protected RedstoneConduitNetwork network;

  protected final Set<Signal> externalSignals = new HashSet<Signal>();

  protected boolean neighbourDirty = true;

  public RedstoneConduit() {
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(EnderIO.itemRedstoneConduit, 1, 0);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    this.network = (RedstoneConduitNetwork) network;
    return true;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
    return false;
  }

  @Override
  public void updateNetwork() {
    World world = getBundle().getEntity().getWorldObj();
    if(world != null) {
      updateNetwork(world);
    }
  }

  protected boolean acceptSignalsForDir(ForgeDirection dir) {
    BlockCoord loc = getLocation().getLocation(dir);
    return ConduitUtil.getConduit(getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, IRedstoneConduit.class) == null;
  }

  @Override
  public Set<Signal> getNetworkInputs() {
    return getNetworkInputs(null);
  }

  @Override
  public Set<Signal> getNetworkInputs(ForgeDirection side) {
    if(network != null) {
      network.setNetworkEnabled(false);
    }

    Set<Signal> res = new HashSet<Signal>();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if((side == null || dir == side) && acceptSignalsForDir(dir)) {
        int input = getExternalPowerLevel(dir);
        if(input > 1) { // need to degrade external signals by one as they
                        // enter
          BlockCoord loc = getLocation().getLocation(dir);
          Signal signal = new Signal(loc.x, loc.y, loc.z, dir, input - 1, getSignalColor(dir));
          res.add(signal);
        }
      }
    }

    if(network != null) {
      network.setNetworkEnabled(true);
    }

    return res;
  }

  @Override
  public DyeColor getSignalColor(ForgeDirection dir) {
    return DyeColor.RED;
  }

  @Override
  public Set<Signal> getNetworkOutputs(ForgeDirection side) {
    if(network == null) {
      return Collections.emptySet();
    }
    return network.getSignals();
  }

  @Override
  public boolean onNeighborBlockChange(Block blockId) {
    World world = getBundle().getEntity().getWorldObj();
    if(world.isRemote) {
      return false;
    }
    boolean res = super.onNeighborBlockChange(blockId);
    if(network == null || network.updatingNetwork) {
      return false;
    }
    neighbourDirty = blockId != EnderIO.blockConduitBundle;
    return res;
  }

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(!world.isRemote && neighbourDirty) {
      network.destroyNetwork();
      updateNetwork(world);
      neighbourDirty = false;
    }
  }

  //returns 16 for string power inputs
  protected int getExternalPowerLevel(ForgeDirection dir) {
    World world = getBundle().getEntity().getWorldObj();
    BlockCoord loc = getLocation();
    loc = loc.getLocation(dir);

    int strong = world.isBlockProvidingPowerTo(loc.x, loc.y, loc.z, dir.ordinal());
    if(strong > 0) {
      return 16;
    }

    int res = world.getIndirectPowerLevelTo(loc.x, loc.y, loc.z, dir.ordinal());
    if(res < 15 && world.getBlock(loc.x, loc.y, loc.z) == Blocks.redstone_wire) {
      int wireIn = world.getBlockMetadata(loc.x, loc.y, loc.z);
      res = Math.max(res, wireIn);
    }
    return res;
  }

  @Override
  public int isProvidingStrongPower(ForgeDirection toDirection) {
    return 0;
  }

  @Override
  public int isProvidingWeakPower(ForgeDirection toDirection) {
    if(network == null || !network.isNetworkEnabled()) {
      return 0;
    }
    int result = 0;
    for (Signal signal : getNetworkOutputs(toDirection.getOpposite())) {
      result = Math.max(result, signal.strength);
    }
    return result;
  }

  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return isActive() ? ICONS.get(KEY_CORE_ON_ICON) : ICONS.get(KEY_CORE_OFF_ICON);
    }
    return isActive() ? ICONS.get(KEY_TRANSMISSION_ICON) : ICONS.get(KEY_CONDUIT_ICON);
  }

  @Override
  public IIcon getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  public String toString() {
    return "RedstoneConduit [network=" + network + " connections=" + conduitConnections + " active=" + active + "]";
  }

  @Override
  public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {
    int[] result = new int[16];
    Set<Signal> outs = getNetworkOutputs(side);
    if(outs != null) {
      for (Signal s : outs) {
        result[s.color.ordinal()] = s.strength;
      }
    }
    return result;
  }

  @Override
  public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {
    Set<Signal> outs = getNetworkOutputs(side);
    if(outs != null) {
      for (Signal s : outs) {
        if(subnet == s.color.ordinal()) {
          return s.strength;
        }
      }
    }
    return 0;
  }
  
  @Override
  public boolean onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {    
    return false;
  }

}
