package crazypants.enderio.machine.spawner;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import crazypants.enderio.EnderIO;

public class BrokenSpawnerRenderer implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {    
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    RenderBlocks rb = (RenderBlocks)data[0];
    rb.setOverrideBlockTexture(EnderIO.itemBrokenSpawner.getIconFromDamage(0));
    rb.renderBlockAsItem(Blocks.stone, 0, 1);
    rb.setOverrideBlockTexture(null); 
    GL11.glDisable(GL11.GL_ALPHA_TEST);
  }

}
