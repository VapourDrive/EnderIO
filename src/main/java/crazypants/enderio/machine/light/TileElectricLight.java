package crazypants.enderio.machine.light;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class TileElectricLight extends TileEntityEio implements IInternalPowerReceptor {

  private ForgeDirection face = ForgeDirection.DOWN;

  public static final float MJ_USE_PER_TICK = 0.05f;

  protected PowerHandler powerHandler;

  private boolean init = true;

  private List<TileLightNode> lightNodes;

  private int[] lightNodeCoords;

  private boolean updatingLightNodes = false;

  private boolean lastActive = false;

  private boolean isInvereted;

  private boolean requiresPower = true;

  public TileElectricLight() {
    powerHandler = PowerHandlerUtil.createHandler(Capacitors.BASIC_CAPACITOR.capacitor, this, Type.MACHINE);
  }

  public void onNeighborBlockChange(Block blockID) {
    init = true;
  }

  public void nodeNeighbourChanged(TileLightNode tileLightNode) {
    init = true;
  }

  public void nodeRemoved(TileLightNode tileLightNode) {
    if(!updatingLightNodes) {
      init = true;
    }
  }

  public ForgeDirection getFace() {
    return face;
  }

  public void setFace(ForgeDirection face) {
    this.face = face;
  }

  public void setInverted(boolean inverted) {
    isInvereted = inverted;
  }
  
  public void setRequiresPower(boolean isPowered) {
    requiresPower = isPowered;    
  }

  public boolean isRequiresPower() {
    return requiresPower;
  }  

  public boolean isInvereted() {
    return isInvereted;
  }

  public void setInvereted(boolean isInvereted) {
    this.isInvereted = isInvereted;
  }


  @Override
  public void updateEntity() {
    if(worldObj.isRemote) {
      return;
    }

    boolean hasRedstone = hasRedstoneSignal();
    if(requiresPower) {
      double stored = powerHandler.getEnergyStored();
      powerHandler.update();
      powerHandler.setEnergy(stored);
    }

    boolean isActivated = (requiresPower ? hasPower() : true) && (hasRedstone && !isInvereted || !hasRedstone && isInvereted);
    if(isActivated && requiresPower) {
      powerHandler.setEnergy(Math.max(0, powerHandler.getEnergyStored() - MJ_USE_PER_TICK));
    }
    if(!hasPower() && requiresPower) {
      isActivated = false;
    }

    if(init && requiresPower) {
      updateLightNodes();
    }

    if(isActivated != lastActive || init) {
      worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isActivated ? 1 : 0, 2);

      if(requiresPower) {
        for (TileLightNode ln : lightNodes) {
          if(ln != null) {
            worldObj.setBlockMetadataWithNotify(ln.xCoord, ln.yCoord, ln.zCoord, isActivated ? 1 : 0, 2);
            worldObj.markBlockForUpdate(ln.xCoord, ln.yCoord, ln.zCoord);
            worldObj.updateLightByType(EnumSkyBlock.Block, ln.xCoord, ln.yCoord, ln.zCoord);
          }
        }
      }
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
      init = false;
      lastActive = isActivated;
    }
  }

  public void onBlockRemoved() {
    if(!requiresPower) {
      return;
    }
    updatingLightNodes = true;
    try {
      clearLightNodes();
    } finally {
      updatingLightNodes = false;
    }
  }

  private void updateLightNodes() {
    if(!requiresPower) {
      return;
    }
    updatingLightNodes = true;
    List<NodeEntry> before = new ArrayList<NodeEntry>(17);
    if(lightNodes != null) {
      for (TileLightNode node : lightNodes) {
        before.add(new NodeEntry(node));
      }
    }
    List<NodeEntry> after = new ArrayList<NodeEntry>(17);
    try {
      if(lightNodeCoords != null) {

        // just loaded
        lightNodes = new ArrayList<TileLightNode>();
        for (int i = 0; i < lightNodeCoords.length; i += 3) {
          TileEntity te = worldObj.getTileEntity(lightNodeCoords[i], lightNodeCoords[i + 1], lightNodeCoords[i + 2]);
          if(te instanceof TileLightNode) {
            lightNodes.add((TileLightNode) te);
          }
        }
        lightNodeCoords = null;

      } else if(lightNodes == null) { // just created

        lightNodes = new ArrayList<TileLightNode>();

      }

      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir != face && dir != face.getOpposite()) { // don't project behind
          // us
          Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
          addNodeInDirection(new Vector3d(offset), after);
          addNodeInDirection(offset.add(ForgeDirectionOffsets.forDirCopy(face.getOpposite())), after);
        }
      }

      addNodeInDirection(ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      Vector3d[] diags = new Vector3d[2];
      if(face.offsetX != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.SOUTH);
      } else if(face.offsetY != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.EAST);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.SOUTH);
      } else {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.EAST);
      }
      addDiaganals(diags, new Vector3d(), after);
      addDiaganals(diags, ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      if(!areEqual(before, after)) {

        clearLightNodes();

        for (NodeEntry entry : after) {
          worldObj.setBlock(entry.coord.x, entry.coord.y, entry.coord.z, EnderIO.blockLightNode);
          TileEntity te = worldObj.getTileEntity(entry.coord.x, entry.coord.y, entry.coord.z);
          if(te instanceof TileLightNode) {
            TileLightNode ln = (TileLightNode) te;
            ln.parentX = xCoord;
            ln.parentY = yCoord;
            ln.parentZ = zCoord;
            ln.isDiagnal = entry.isDiagnal;
            lightNodes.add(ln);
          }
        }

      } else {
        init = false;
      }

    } finally {
      updatingLightNodes = false;
    }
  }

  private boolean areEqual(List<NodeEntry> before, List<NodeEntry> after) {
    if(before.size() != after.size()) {
      return false;
    }
    for (NodeEntry entry : before) {
      if(!after.contains(entry)) {
        return false;
      }
    }
    return true;
  }

  private void addDiaganals(Vector3d[] diags, Vector3d trans, List<NodeEntry> result) {
    Vector3d offset = new Vector3d();
    offset.set(diags[0]);
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);

    offset.set(diags[0]);
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);

    offset.set(new Vector3d(diags[0]).negate());
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);

    offset.set(new Vector3d(diags[0]).negate());
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);
  }

  private void addNodeInDirection(Vector3d offset, List<NodeEntry> after) {
    addNodeInDirection(offset, false, after);
  }

  private void addNodeInDirection(Vector3d offset, boolean diagnal, List<NodeEntry> result) {

    boolean isAir = isAir(offset);
    boolean isTransp = isTranparent(offset);
    if(isAir || isTransp) {
      offset.scale(2);
      if(isAir(offset)) {
        addLightNode(offset, diagnal, result);
      } else if(isAir) {
        offset.scale(0.5);
        addLightNode(offset, diagnal, result);
      }
    }
  }

  private boolean isLightNode(Vector3d offset) {
    return worldObj.getBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == EnderIO.blockLightNode;
  }

  private void clearLightNodes() {
    if(lightNodes != null) {
      for (TileLightNode ln : lightNodes) {
        if(worldObj.getBlock(ln.xCoord, ln.yCoord, ln.zCoord) == EnderIO.blockLightNode) {
          worldObj.setBlockToAir(ln.xCoord, ln.yCoord, ln.zCoord);
        }
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, boolean isDiag, List<NodeEntry> result) {

    int x = xCoord + (int) offset.x;
    int y = yCoord + (int) offset.y;
    int z = zCoord + (int) offset.z;

    if(isLightNode(offset)) {
      TileLightNode te = (TileLightNode) worldObj.getTileEntity(x, y, z);
      if(te.parentX != xCoord || te.parentY != yCoord || te.parentZ != zCoord) {
        // its somebody else's so leave it alone
        return;
      }
    }
    result.add(new NodeEntry(new BlockCoord(x, y, z), isDiag));
  }

  private boolean isRailcraftException(Block id) {

    String className = id.getClass().getName();
    if(className.equals("mods.railcraft.common.blocks.machine.BlockMachine")) {
      return true;
    }

    return false;
  }

  private boolean isTranparent(Vector3d offset) {
    Block id = worldObj.getBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z);
    if(isRailcraftException(id)) {
      return false;
    }
    return worldObj.getBlockLightOpacity(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == 0;
  }

  private boolean isAir(Vector3d offset) {
    return worldObj.isAirBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) || isLightNode(offset);
  }

  @Override
  public void readCustomNBT(NBTTagCompound root) {

    face = ForgeDirection.values()[root.getShort("face")];
    isInvereted = root.getBoolean("isInverted");
    requiresPower = root.getBoolean("requiresPower");

    float storedEnergy = root.getFloat("storedEnergy");
    powerHandler.setEnergy(storedEnergy);
    lightNodeCoords = root.getIntArray("lightNodes");
  }

  @Override
  public void writeCustomNBT(NBTTagCompound root) {

    root.setShort("face", (short) face.ordinal());
    root.setFloat("storedEnergy", (float) powerHandler.getEnergyStored());
    root.setBoolean("isInverted", isInvereted);
    root.setBoolean("requiresPower", requiresPower);

    if(lightNodes != null) {
      int[] lnLoc = new int[lightNodes.size() * 3];
      int index = 0;
      for (TileLightNode ln : lightNodes) {
        lnLoc[index++] = ln.xCoord;
        lnLoc[index++] = ln.yCoord;
        lnLoc[index++] = ln.zCoord;
      }
      root.setIntArray("lightNodes", lnLoc);
    }
  }

  public boolean hasPower() {
    return powerHandler.getEnergyStored() > MJ_USE_PER_TICK;
  }

  private boolean hasRedstoneSignal() {
    return worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    if(!requiresPower) {
      return null;
    }
    return powerHandler.getPowerReceiver();
  }

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  // RF Power

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(!requiresPower) {
      return 0;
    }
    return PowerHandlerUtil.recieveRedstoneFlux(from, powerHandler, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    if(!requiresPower) {
      return false;
    }
    return true;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    if(!requiresPower) {
      return 0;
    }
    return (int) (powerHandler.getEnergyStored() * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    if(!requiresPower) {
      return 0;
    }
    return (int) (powerHandler.getMaxEnergyStored() * 10);
  }

  static class NodeEntry {
    final BlockCoord coord;
    final boolean isDiagnal;

    NodeEntry(BlockCoord coord, boolean isDiagnal) {
      this.coord = coord;
      this.isDiagnal = isDiagnal;
    }

    NodeEntry(TileLightNode node) {
      coord = new BlockCoord(node);
      isDiagnal = node.isDiagnal;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((coord == null) ? 0 : coord.hashCode());
      result = prime * result + (isDiagnal ? 1231 : 1237);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      NodeEntry other = (NodeEntry) obj;
      if(coord == null) {
        if(other.coord != null) {
          return false;
        }
      } else if(!coord.equals(other.coord)) {
        return false;
      }
      if(isDiagnal != other.isDiagnal) {
        return false;
      }
      return true;
    }

  }

}
