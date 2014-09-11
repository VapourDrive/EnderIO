package crazypants.enderio.conduit;

import static crazypants.enderio.ModObject.blockPainter;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadePainterRecipe;
import crazypants.enderio.conduit.item.filter.ClearFilterRecipe;
import crazypants.enderio.conduit.item.filter.CopyFilterRecipe;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.Material;

public class ConduitRecipes {

  public static void addRecipes() {

    //Crafting Components
    ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 0);
    ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 1, Material.CONDUIT_BINDER.ordinal());

    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack fusedGlass = new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.GLASS.ordinal());

    ItemStack conductiveIron = new ItemStack(EnderIO.itemAlloy, 1, Alloy.CONDUCTIVE_IRON.ordinal());
    ItemStack energeticGold = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ENERGETIC_ALLOY.ordinal());
    ItemStack phasedGold = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack phasedIron = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_IRON.ordinal());
    ItemStack phasedIronNugget = new ItemStack(EnderIO.itemMaterial, 1, Material.PHASED_IRON_NUGGET.ordinal());
    ItemStack redstoneAlloy = new ItemStack(EnderIO.itemAlloy, 1, Alloy.REDSTONE_ALLOY.ordinal());
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());

    //Recipes
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemConduitFacade, 1, 0), "bbb", "b b", "bbb", 'b', conduitBinder);

    int numConduits = Config.numConduitsPerRecipe;
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemLiquidConduit, numConduits, 0), "bbb", "###", "bbb", 'b', conduitBinder, '#', fusedGlass);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemLiquidConduit, numConduits, 1), "bbb", "###", "bbb", 'b', conduitBinder, '#', fusedQuartz);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemLiquidConduit, numConduits, 2), "bbb", "#p#", "bbb", 'b', conduitBinder, '#', fusedQuartz, 'p', phasedGold);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemPowerConduit, numConduits, 0), "bbb", "###", "bbb", 'b', conduitBinder, '#', conductiveIron);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemPowerConduit, numConduits, 1), "bbb", "###", "bbb", 'b', conduitBinder, '#', energeticGold);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemPowerConduit, numConduits, 2), "bbb", "###", "bbb", 'b', conduitBinder, '#', phasedGold);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemRedstoneConduit, numConduits, 0), "   ", "###", "   ", 'b', conduitBinder, '#', redstoneAlloy);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemRedstoneConduit, 1, 1), "lbl", "bcb", "lbl", 'b', conduitBinder, 'c', redstoneConduit, 'l',
        Blocks.lever);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemRedstoneConduit, numConduits, 2), "bbb", "###", "bbb", 'b', conduitBinder, '#',
        redstoneAlloy);
    GameRegistry.addShapedRecipe(new ItemStack(EnderIO.itemGasConduit, numConduits, 0), "bbb", "#g#", "bbb", 'b', conduitBinder, '#',
        electricalSteel, 'g', fusedGlass);

    ItemStack itemConduit = new ItemStack(EnderIO.itemItemConduit, numConduits, 0);
    GameRegistry.addShapedRecipe(itemConduit, "bbb", "###", "bbb", 'b', conduitBinder, '#', phasedIronNugget);

    MachineRecipeRegistry.instance.registerRecipe(blockPainter.unlocalisedName, new FacadePainterRecipe());

    //Filter Recipes
    ItemStack basicFilter = new ItemStack(EnderIO.itemBasicFilterUpgrade, 1, 0);
    GameRegistry.addShapedRecipe(basicFilter, " p ", "php", " p ", 'p', Items.paper, 'h', Blocks.hopper);

    ItemStack advFilter = new ItemStack(EnderIO.itemBasicFilterUpgrade, 1, 1);
    GameRegistry.addRecipe(new ShapedOreRecipe(advFilter, "rpr", "php", "rpr", 'p', Items.paper, 'h', "itemSkull", 'r', Items.redstone));

    ItemStack modFilter = new ItemStack(EnderIO.itemModItemFilter, 1, 0);
    GameRegistry.addShapedRecipe(modFilter, " p ", "pwp", " p ", 'p', Items.paper, 'w', EnderIO.itemYetaWench);

    ItemStack exFilt = new ItemStack(EnderIO.itemExistingItemFilter);
    GameRegistry.addShapedRecipe(exFilt, "rpr", "pcp", "rpr", 'p', Items.paper, 'c', new ItemStack(Items.comparator, 1, 0), 'r', Items.redstone);

    ClearFilterRecipe clearRec = new ClearFilterRecipe();
    MinecraftForge.EVENT_BUS.register(clearRec);
    GameRegistry.addRecipe(clearRec);

    CopyFilterRecipe copyRec = new CopyFilterRecipe();
    GameRegistry.addRecipe(copyRec);

    ItemStack speedUpgrade = new ItemStack(EnderIO.itemExtractSpeedUpgrade, 1, 0);
    GameRegistry.addShapedRecipe(speedUpgrade, "iii","epe","ere", 'p', Blocks.piston, 'e', electricalSteel, 'r', Blocks.redstone_torch, 'i', Items.iron_ingot);
  }

}
