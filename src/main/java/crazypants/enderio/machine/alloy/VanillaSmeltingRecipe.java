package crazypants.enderio.machine.alloy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;

public class VanillaSmeltingRecipe implements IMachineRecipe {

  // We will use the same energy as per a standard furnace.
  // To do the conversion between fuel burning and MJ, use the Stirling Gen
  // which produces one MJ per tick of burn time
  private static float MJ_PER_ITEM = TileEntityFurnace.getItemBurnTime(new ItemStack(Items.coal, 1, 0)) / 8;

  private boolean enabled = true;

  private List<RecipeInput> excludes = new ArrayList<RecipeInput>();

  @Override
  public String getUid() {
    return "VanillaSmeltingRecipe";
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void addExclude(RecipeInput ri) {
    excludes.add(ri);
  }

  @Override
  public float getEnergyRequired(MachineRecipeInput... inputs) {
    int numInputs = getNumInputs(inputs);
    return numInputs * MJ_PER_ITEM;
  }

  private int getNumInputs(MachineRecipeInput[] inputs) {
    int numInputs = 0;
    for (MachineRecipeInput input : inputs) {
      if(input != null && isValidInput(input)) {
        numInputs += input.item.stackSize;
      }
    }
    return Math.min(numInputs, 3);
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    if(!enabled) {
      return false;
    }
    ItemStack output = null;
    for (MachineRecipeInput ri : inputs) {
      if(ri != null && ri.item != null && !isExcluded(ri.item)) {
        if(output == null) {
          output = FurnaceRecipes.smelting().getSmeltingResult(ri.item);
          if(output == null) {
            return false;
          }
        } else {
          ItemStack newOutput = FurnaceRecipes.smelting().getSmeltingResult(ri.item);
          if(newOutput == null || !newOutput.isItemEqual(output)) {
            return false;
          }
        }
      }
    }
    return output != null;
  }

  private boolean isExcluded(ItemStack item) {
    for (RecipeInput ri : excludes) {
      if(ri != null && ri.isInput(item)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    ItemStack output = null;
    int inputCount = 0;
    for (MachineRecipeInput ri : inputs) {
      if(ri != null && ri.item != null && output == null) {
        output = FurnaceRecipes.smelting().getSmeltingResult(ri.item);
      }
    }
    if(output == null) {
      return new ResultStack[0];
    }
    ItemStack result = output.copy();
    result.stackSize = result.stackSize * getNumInputs(inputs);
    return new ResultStack[] { new ResultStack(result) };
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    if(output == null) {
      return 0;
    }
    float result = FurnaceRecipes.smelting().func_151398_b(output);
    return result * output.stackSize;
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(!enabled) {
      return false;
    }
    if(input == null || input.item == null) {
      return false;
    }
    if(isExcluded(input.item)) {
      return false;
    }
    ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(input.item);
    return itemstack != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    int consumed = 0;
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();
    for (MachineRecipeInput ri : inputs) {
      if(isValidInput(new MachineRecipeInput(ri.slotNumber, ri.item)) && consumed < 3 && ri != null && ri.item != null) {
        int available = ri.item.stackSize;
        int canUse = 3 - consumed;
        int use = Math.min(canUse, available);
        if(use > 0) {
          ItemStack st = ri.item.copy();
          st.stackSize = use;
          result.add(new MachineRecipeInput(ri.slotNumber, st));
          consumed += use;
        }
      }
    }
    return result;
  }
  
  
  public List<IRecipe> getAllRecipes() {
    if(!enabled) {
      return Collections.emptyList();
    }
    List<IRecipe> result = new ArrayList<IRecipe>();
    Map<ItemStack, ItemStack> metaList = FurnaceRecipes.smelting().getSmeltingList();
    for (Entry<ItemStack, ItemStack> entry : metaList.entrySet()) {
      result.add(new Recipe(new RecipeInput(entry.getKey()), MJ_PER_ITEM, new RecipeOutput(entry.getValue())));
    }
    return result;
  }

}
