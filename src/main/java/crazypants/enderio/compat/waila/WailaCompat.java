package crazypants.enderio.compat.waila;

import static crazypants.enderio.compat.waila.IWailaInfoProvider.*;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;

public class WailaCompat implements IWailaDataProvider {

  public static final WailaCompat INSTANCE = new WailaCompat();

  public static void load(IWailaRegistrar registrar) {
    registrar.registerHeadProvider(INSTANCE, Block.class);
    registrar.registerBodyProvider(INSTANCE, Block.class);
    registrar.registerTailProvider(INSTANCE, Block.class);
  }

  @Override
  public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return null;
  }

  @Override
  public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }

  @Override
  public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

    Block block = accessor.getBlock();
    Item item = Item.getItemFromBlock(block);
    EntityPlayer player = accessor.getPlayer();
    World world = player.worldObj;

    if(block instanceof IWailaInfoProvider) {
      IWailaInfoProvider info = (IWailaInfoProvider) block;
      MovingObjectPosition pos = accessor.getPosition();

      if(block instanceof IAdvancedTooltipProvider) {
        int mask = info.getDefaultDisplayMask(world, pos.blockX, pos.blockY, pos.blockZ);
        boolean basic = (mask & BIT_BASIC) == BIT_BASIC;
        boolean common = (mask & BIT_COMMON) == BIT_COMMON;
        boolean detailed = (mask & BIT_DETAILED) == BIT_DETAILED;

        IAdvancedTooltipProvider adv = (IAdvancedTooltipProvider) block;

        if(common) {
          adv.addCommonEntries(itemStack, player, currenttip, false);
        }

        if(TooltipAddera.showAdvancedTooltips() && detailed) {
          adv.addDetailedEntries(itemStack, player, currenttip, false);
        } else if(detailed) { // show "<Hold Shift>"
          TooltipAddera.addShowDetailsTooltip(currenttip);
        }

        if(!TooltipAddera.showAdvancedTooltips() && basic) {
          adv.addBasicEntries(itemStack, player, currenttip, false);
        }
      } else if(block instanceof IResourceTooltipProvider) {
        TooltipAddera.addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
      }

      info.getWailaInfo(currenttip, world, pos.blockX, pos.blockY, pos.blockZ);
    }

    else {

      if(block instanceof IAdvancedTooltipProvider) {
        TooltipAddera.addInformation((IAdvancedTooltipProvider) block, itemStack, player, currenttip, false);
      } else if(item instanceof IAdvancedTooltipProvider) {
        TooltipAddera.addInformation((IAdvancedTooltipProvider) item, itemStack, player, currenttip, false);
      } else if(block instanceof IResourceTooltipProvider) {
        TooltipAddera.addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
      }
    }
    return currenttip;
  }

  @Override
  public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }
}
