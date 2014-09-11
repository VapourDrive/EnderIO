package crazypants.enderio.machine.killera;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketSwing extends MessageTileEntity<TileKillerJoe> implements IMessageHandler<PacketSwing, IMessage> {

  public PacketSwing() {
  }

  public PacketSwing(TileKillerJoe tile) {
    super(tile);
  }

  @Override
  public IMessage onMessage(PacketSwing message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileKillerJoe tile = message.getTileEntity(player.worldObj);
    if (tile != null) {
      tile.swingWeapon();
    }
    return null;
  }


}
