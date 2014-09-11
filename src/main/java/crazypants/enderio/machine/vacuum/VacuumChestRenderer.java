package crazypants.enderio.machine.vacuum;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class VacuumChestRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    Tessellator.instance.startDrawingQuads();
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.6, 0.6, 0.6), EnderIO.blockHyperCube.getIcon(0, 0));
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.90, 0.90,0.90), EnderIO.blockVacuumChest.getIcon(0, 0));
    Tessellator.instance.draw();
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    
    double size = 0.4;    
    renderer.renderMinX = 0.5 - size;
    renderer.renderMaxX = 0.5 + size;
    renderer.renderMinY = 0.5 - size;
    renderer.renderMaxY = 0.5 + size;
    renderer.renderMinZ = 0.5 - size;
    renderer.renderMaxZ = 0.5 + size;
    renderer.lockBlockBounds = true;
    
    renderer.setOverrideBlockTexture(EnderIO.blockHyperCube.getIcon(0, 0));
    renderer.renderStandardBlock(Blocks.stone, x, y, z);
    renderer.setOverrideBlockTexture(null);
    
    renderer.lockBlockBounds = false;
    
    renderer.renderMinX = 0;
    renderer.renderMaxX = 1;
    renderer.renderMinY = 0;
    renderer.renderMaxY = 1;
    renderer.renderMinZ = 0;
    renderer.renderMaxZ = 1;
    
    renderer.setOverrideBlockTexture(EnderIO.blockVacuumChest.getIcon(0, 0));
    renderer.renderStandardBlock(Blocks.stone, x, y, z);
    renderer.setOverrideBlockTexture(null);
    
    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {    
    return true;
  }

  @Override
  public int getRenderId() {
    return EnderIO.blockVacuumChest.renderId;
  }
  
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
    renderInventoryBlock(Block.getBlockFromItem(item.getItem()), item.getItemDamage(), 0, (RenderBlocks)data[0]);    
  }

}
