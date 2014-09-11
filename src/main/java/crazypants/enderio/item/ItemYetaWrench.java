package crazypants.enderio.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.network.PacketHandler;

public class ItemYetaWrench extends Item implements IToolWrench, IResourceTooltipProvider {

  public static ItemYetaWrench create() {
    if(Config.useSneakMouseWheelYetaWrench) {
      PacketHandler.INSTANCE.registerMessage(YetaWrenchPacketProcessor.class, YetaWrenchPacketProcessor.class, PacketHandler.nextID(), Side.SERVER);
    }
    ItemYetaWrench result = new ItemYetaWrench();
    result.init();
    return result;
  }

  protected ItemYetaWrench() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemYetaWrench.unlocalisedName);
    setMaxStackSize(1);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemYetaWrench.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:yetaWrench");
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    Block block = world.getBlock(x, y, z);
    if(block != null && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side)) && !player.isSneaking()) {
      player.swingItem();
      return !world.isRemote;
    }
    return false;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
    if(!Config.useSneakRightClickYetaWrench) {
      return equipped;
    }
    if(!player.isSneaking()) {
      return equipped;
    }
    ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
    if(curMode == null) {
      curMode = ConduitDisplayMode.ALL;
    }
    ConduitDisplayMode newMode = curMode.next();
    ConduitDisplayMode.setDisplayMode(equipped, newMode);
    return equipped;
  }

  @Override
  public boolean canWrench(EntityPlayer player, int x, int y, int z) {
    return true;
  }

  @Override
  public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
    player.swingItem();
  }

  @Override
  public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
    return true;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }
  
  @Override
  public boolean isFull3D() {
    return true;
  }
}
