package crazypants.enderio.machine.enchanter;

import crazypants.enderio.machine.recipe.RecipeInput;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class EnchanterRecipe {

  private final RecipeInput input;
  private final Enchantment enchantment;
  private final int costPerLevel;
  
  public static Enchantment getEnchantmentFromName(String enchantmentName) {    
    for(Enchantment ench : Enchantment.enchantmentsList) {
      if(ench != null && ench.getName() != null && ench.getName().equals(enchantmentName)) {        
        return ench;
      }
    }
    return null;
  }
  
  public EnchanterRecipe(RecipeInput curInput, String enchantmentName, int costPerLevel) {
    this.input = curInput;
    enchantment = getEnchantmentFromName(enchantmentName);
    this.costPerLevel = costPerLevel;
  }

  public EnchanterRecipe(RecipeInput input, Enchantment enchantment, int costPerLevel) {  
    this.input = input;
    this.enchantment = enchantment;
    this.costPerLevel = costPerLevel;
  }
  
  public boolean isInput(ItemStack stack) {
    if(stack == null || !isValid()) {
      return false;
    }
    return input.isInput(stack);    
  }
  
  public boolean isValid() {
    return enchantment != null && input != null && input.getInput() != null && costPerLevel > -1;
  }

  public Enchantment getEnchantment() {
    return enchantment;
  }

  public RecipeInput getInput() {
    return input;
  }

  public int getCostPerLevel() {
    return costPerLevel;
  }
  
  
  
  
}
