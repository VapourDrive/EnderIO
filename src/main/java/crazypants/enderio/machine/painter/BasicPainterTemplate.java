package crazypants.enderio.machine.painter;

import static crazypants.enderio.machine.MachineRecipeInput.*;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.util.Util;

public abstract class BasicPainterTemplate implements IMachineRecipe {

  // 5 seconds at the default energy use of 2 mj per tick.
  public static float DEFAULT_ENERGY_PER_TASK = 200;

  public static boolean isValidSourceDefault(ItemStack paintSource) {
    if(paintSource == null) {
      return false;
    }
    Block block = Util.getBlockFromItemId(paintSource);
    if(block == null) {
      return false;
    }
    if(!Config.allowTileEntitiesAsPaintSource && block instanceof ITileEntityProvider) {
      return false;
    }
    
    return block.isOpaqueCube() || (block.getMaterial().isOpaque() && block.renderAsNormalBlock()) || block == Blocks.glass;
  }

  protected final Block[] validTargets;

  protected BasicPainterTemplate(Block... validTargetBlocks) {
    this.validTargets = validTargetBlocks;
  }

  @Override
  public float getEnergyRequired(MachineRecipeInput... inputs) {
    return DEFAULT_ENERGY_PER_TASK;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    return isValidTarget(getTarget(inputs)) && isValidPaintSource(getPaintSource(inputs));
  }

  @Override
  public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    ItemStack target = getTarget(inputs);
    ItemStack paintSource = getPaintSource(inputs);
    if(target == null || paintSource == null) {
      return null;
    }
    ItemStack result = new ItemStack(getResultId(target), 1, target.getItemDamage());
    PainterUtil.setSourceBlock(result, Util.getBlockFromItemId(paintSource), paintSource.getItemDamage());
    return new ResultStack[] { new ResultStack(result) };
  }

  public ItemStack getTarget(MachineRecipeInput... inputs) {
    return getInputForSlot(0, inputs);
  }

  public ItemStack getPaintSource(MachineRecipeInput... inputs) {
    return getInputForSlot(1, inputs);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    if(input.slotNumber == 0) {
      return isValidTarget(input.item);
    }
    if(input.slotNumber == 1) {
      return isValidPaintSource(input.item);
    }
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPainter.unlocalisedName;
  }

  public boolean isValidPaintSource(ItemStack paintSource) {
    return isValidSourceDefault(paintSource);
  }

  public boolean isValidTarget(ItemStack target) {
    // first check for exact matches, then check for item blocks
    if(target == null) {
      return false;
    }
    
    Block blk = Block.getBlockFromItem(target.getItem());
    if(blk == null) {
      return false;
    }

    for (int i = 0; i < validTargets.length; i++) {
      if(validTargets[i] == blk) {
        return true;
      }
    }
    
    return false;
  }

  @Override
  public String getUid() {
    return getClass().getCanonicalName();
  }

  protected Item getResultId(ItemStack target) {
    return target.getItem();
  }

  public int getQuantityConsumed(MachineRecipeInput input) {
    return input.slotNumber == 0 ? 1 : 0;
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    MachineRecipeInput consume = null;
    for (MachineRecipeInput input : inputs) {
      if(input != null && input.slotNumber == 0 && input.item != null) {
        ItemStack consumed = input.item.copy();
        consumed.stackSize = 1;
        consume = new MachineRecipeInput(input.slotNumber, consumed);
      }
    }
    if(consume != null) {
      return Collections.singletonList(consume);
    }
    return null;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    return 0;
  }

}
