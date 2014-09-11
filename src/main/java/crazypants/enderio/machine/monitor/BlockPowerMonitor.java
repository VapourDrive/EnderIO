package crazypants.enderio.machine.monitor;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.DyeColor;

public class BlockPowerMonitor extends AbstractMachineBlock<TilePowerMonitor> implements ITileEntityProvider {

  public static BlockPowerMonitor create() {

    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitor.class,PacketPowerMonitor.class,PacketHandler.nextID(),Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerInfo.class,PacketPowerInfo.class,PacketHandler.nextID(),Side.CLIENT);

    BlockPowerMonitor result = new BlockPowerMonitor();
    result.init();
    return result;
  }

  protected BlockPowerMonitor() {
    super(ModObject.blockPowerMonitor, TilePowerMonitor.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerPowerMonitor();    
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TilePowerMonitor) {
      return new GuiPowerMonitor((TilePowerMonitor) te);
    }
    return null;

  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_POWER_MONITOR;
  }


  @Override
  public boolean canProvidePower() {
    return true;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:powerMonitor";
  }

  @Override
  public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
      int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TilePowerMonitor) {

      return ((TilePowerMonitor) te).getRednetOutputValue(ForgeDirection.values()[side], DyeColor.RED.ordinal());
    }
    return 0;
  }

}
