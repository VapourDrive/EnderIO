package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketPowerStorage implements IMessage, IMessageHandler<PacketPowerStorage, IMessage> {

  private int x;
  private int y;
  private int z;
  private float storedEnergy;

  public PacketPowerStorage() {
  }

  public PacketPowerStorage(AbstractMachineEntity ent) {
    x = ent.xCoord;
    y = ent.yCoord;
    z = ent.zCoord;
    storedEnergy = ent.storedEnergy;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeFloat(storedEnergy);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    storedEnergy = buf.readFloat();
  }

  @Override
  public IMessage onMessage(PacketPowerStorage message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      me.storedEnergy = message.storedEnergy;
      me.powerHandler.setEnergy(message.storedEnergy);
    }
    return null;
  }

}
