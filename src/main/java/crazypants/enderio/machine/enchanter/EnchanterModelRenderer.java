package crazypants.enderio.machine.enchanter;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.generator.zombie.ModelZombieJar;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class EnchanterModelRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private static final String TEXTURE = "enderio:models/BookStand.png";

  private EnchanterModel model = new EnchanterModel();

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick) {

    World world = te.getWorldObj();
    TileEnchanter gen = (TileEnchanter) te;

    float f = world.getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
    int l = world.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
    int l1 = l % 65536;
    int l2 = l / 65536;
    Tessellator.instance.setColorOpaque_F(f, f, f);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    renderModel(gen.getFacing());    
    GL11.glPopMatrix();
  }

 

  private void renderModel(int facing) {

    GL11.glPushMatrix();

    GL11.glTranslatef(0.5F, 1.5f, 0.5F);
    GL11.glRotatef(180F, 1F, 0F, 0F);
//    GL11.glScalef(1.2f, 0.9f, 1.2f);

    ForgeDirection dir = ForgeDirection.getOrientation(facing);
    if(dir == ForgeDirection.SOUTH) {
      facing = 0;

    } else if(dir == ForgeDirection.WEST) {
      facing = -1;
    }
    GL11.glRotatef(facing * -90F, 0F, 1F, 0F);

    RenderUtil.bindTexture(TEXTURE);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

    GL11.glTranslatef(-0.5F, -1.5f, -0.5F);
    GL11.glPopMatrix();

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
    renderItem(0, 0, 0);
  }

  private void renderItem(float x, float y, float z) {
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, z);
    GL11.glEnable(GL11.GL_BLEND);
    renderModel(ForgeDirection.SOUTH.ordinal());
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glPopMatrix();
  }
}
