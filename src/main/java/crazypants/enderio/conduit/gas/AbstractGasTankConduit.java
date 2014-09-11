package crazypants.enderio.conduit.gas;

import java.util.List;

import mekanism.api.gas.GasStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.util.BlockCoord;

public abstract class AbstractGasTankConduit extends AbstractGasConduit {

  protected ConduitGasTank tank = new ConduitGasTank(0);
  protected boolean stateDirty = false;
  protected long lastEmptyTick = 0;
  protected int numEmptyEvents = 0;

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    if(player.getCurrentEquippedItem() == null) {
      return false;
    }
    AbstractGasTankConduitNetwork<? extends AbstractGasTankConduit> network = getTankNetwork();
    if(ConduitUtil.isToolEquipped(player)) {

      if(!getBundle().getEntity().getWorldObj().isRemote) {

        if(res != null && res.component != null) {

          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);

          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {

            if(getConectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }

            BlockCoord loc = getLocation().getLocation(faceHit);
            IGasConduit n = ConduitUtil.getConduit(getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, IGasConduit.class);
            if(n == null) {
              return false;
            }
            if(!canJoinNeighbour(n)) {
              return false;
            }
            if(!(n instanceof AbstractGasTankConduit)) {
              return false;
            }
            AbstractGasTankConduit neighbour = (AbstractGasTankConduit) n;
            if(neighbour.getGasType() == null || getGasType() == null) {
              GasStack type = getGasType();
              type = type != null ? type : neighbour.getGasType();
              neighbour.setGasTypeOnNetwork(neighbour, type);
              setGasTypeOnNetwork(this, type);
            }
            return ConduitUtil.joinConduits(this, faceHit);
          } else if(containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if(containsConduitConnection(connDir)) {
            GasStack curGasType = null;
            if(getTankNetwork() != null) {
              curGasType = getTankNetwork().getGasType();
            }
            ConduitUtil.disconectConduits(this, connDir);
            setGasType(curGasType);

          }
        }
      }
      return true;

    }

    return false;
  }

  private void setGasTypeOnNetwork(AbstractGasTankConduit con, GasStack type) {
    AbstractConduitNetwork<?, ?> n = con.getNetwork();
    if(n != null) {
      AbstractGasTankConduitNetwork<?> network = (AbstractGasTankConduitNetwork<?>) n;
      network.setGasType(type);
    }

  }

  protected abstract boolean canJoinNeighbour(IGasConduit n);

  public abstract AbstractGasTankConduitNetwork<? extends AbstractGasTankConduit> getTankNetwork();

  public void setGasType(GasStack gasType) {
    if(tank.getGas() != null && tank.getGas().isGasEqual(gasType)) {
      return;
    }
    if(gasType != null) {
      gasType = gasType.copy();
    } else if(tank.getGas() == null) {
      return;
    }
    tank.setGas(gasType);
    stateDirty = true;
  }

  public ConduitGasTank getTank() {
    return tank;
  }

  public GasStack getGasType() {
    GasStack result = null;
    if(getTankNetwork() != null) {
      result = getTankNetwork().getGasType();
    }
    if(result == null) {
      result = tank.getGas();
    }
    return result;
  }

  protected abstract void updateTank();

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);
    updateTank();
    if(nbtRoot.hasKey("tank")) {
      GasStack gas = GasStack.readFromNBT(nbtRoot.getCompoundTag("tank"));
      tank.setGas(gas);
    } else {
      tank.setGas(null);
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    GasStack gt = getGasType();
    if(GasUtil.isGasValid(gt)) {
      updateTank();
      gt = gt.copy();
      gt.amount = tank.getStored();
      nbtRoot.setTag("tank", gt.write(new NBTTagCompound()));
    }
  }

}
