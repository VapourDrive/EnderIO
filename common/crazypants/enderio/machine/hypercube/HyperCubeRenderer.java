package crazypants.enderio.machine.hypercube;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;

public class HyperCubeRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private IModel model;

  private BoundingBox bb;
  
  private boolean adjustForItem = false;

  public HyperCubeRenderer() {
    float scale = 0.7f;
    if(Config.useAlternateTesseractModel) {
      model = new HyperCubeModel2();
      scale = 0.8f;
      adjustForItem = true;
    } else {
      model = new HyperCubeModel();
    }
    bb = BoundingBox.UNIT_CUBE.scale(scale, scale, scale);
  }
  
  
  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {

    TileHyperCube cube = (TileHyperCube) te;
    
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    
    model.render(cube, x, y, z);
    if (cube.getPowerHandler().getEnergyStored() > 0) {
      renderPower(te.worldObj, x, y, z, cube.getChannel() != null);
    }
    
    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    

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
    if (adjustForItem) {
      switch (type) {
      case ENTITY:
        renderItem(0f, 0f, 0f);
        return;
      case EQUIPPED:
      case EQUIPPED_FIRST_PERSON:
        renderItem(0f, 1f, 1f);
        return;
      case INVENTORY:
        renderItem(0f, 0f, 0f);
        return;
      default:
        renderItem(0f, 0f, 0f);
        return;
      }
    } else {
      renderItem(0, 0, 0);
    }
  }

  private void renderPower(World world, double x, double y, double z, boolean isActive) {

    
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    //GL11.glEnable(GL11.GL_LIGHTING);
//    GL11.glShadeModel(GL11.GL_SMOOTH);
//    GL11.glEnable(GL11.GL_COLOR_MATERIAL);
//    GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_DIFFUSE);
    //RenderHelper.enableGUIStandardItemLighting();
    
    RenderUtil.bindBlockTexture();
    Icon icon = EnderIO.blockHyperCube.getPortalIcon();

    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    if (!isActive) {
      GL11.glColor4f(0, 1, 1, 0.5f);      
    } else {
      GL11.glColor4f(1, 1, 1, 1f);  
    }
    CubeRenderer.render(bb, icon);
    tessellator.draw();

    GL11.glPopMatrix();

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_BLEND);
  }

  private void renderItem(float x, float y, float z) {
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, z);
    model.render();
    GL11.glPopMatrix();
  }

}