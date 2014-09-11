package crazypants.render;

import static net.minecraftforge.common.util.ForgeDirection.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import crazypants.util.BlockCoord;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vector4d;
import crazypants.vecmath.Vector4f;
import crazypants.vecmath.Vertex;

public class RenderUtil {

  public static final Vector3d UP_V = new Vector3d(0, 1, 0);

  public static final Vector3d ZERO_V = new Vector3d(0, 0, 0);

  private static final FloatBuffer MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);

  public static final ResourceLocation BLOCK_TEX = TextureMap.locationBlocksTexture;

  public static final ResourceLocation ITEM_TEX = TextureMap.locationItemsTexture;

  public static final ResourceLocation GLINT_TEX = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  public static void loadMatrix(Matrix4d mat) {
    MATRIX_BUFFER.rewind();
    MATRIX_BUFFER.put((float) mat.m00);
    MATRIX_BUFFER.put((float) mat.m01);
    MATRIX_BUFFER.put((float) mat.m02);
    MATRIX_BUFFER.put((float) mat.m03);
    MATRIX_BUFFER.put((float) mat.m10);
    MATRIX_BUFFER.put((float) mat.m11);
    MATRIX_BUFFER.put((float) mat.m12);
    MATRIX_BUFFER.put((float) mat.m13);
    MATRIX_BUFFER.put((float) mat.m20);
    MATRIX_BUFFER.put((float) mat.m21);
    MATRIX_BUFFER.put((float) mat.m22);
    MATRIX_BUFFER.put((float) mat.m23);
    MATRIX_BUFFER.put((float) mat.m30);
    MATRIX_BUFFER.put((float) mat.m31);
    MATRIX_BUFFER.put((float) mat.m32);
    MATRIX_BUFFER.put((float) mat.m33);
    MATRIX_BUFFER.rewind();
    GL11.glLoadMatrix(MATRIX_BUFFER);
  }

  public static TextureManager engine() {
    return Minecraft.getMinecraft().renderEngine;
  }

  public static void bindItemTexture(ItemStack stack) {
    engine().bindTexture(stack.getItemSpriteNumber() == 0 ? BLOCK_TEX : ITEM_TEX);
  }

  public static void bindItemTexture() {
    engine().bindTexture(ITEM_TEX);
  }

  public static void bindBlockTexture() {
    engine().bindTexture(BLOCK_TEX);
  }

  public static void bindGlintTexture() {
    engine().bindTexture(BLOCK_TEX);
  }

  public static void bindTexture(String string) {
    engine().bindTexture(new ResourceLocation(string));
  }

  public static void bindTexture(ResourceLocation tex) {
    engine().bindTexture(tex);
  }

  public static FontRenderer fontRenderer() {
    return Minecraft.getMinecraft().fontRenderer;
  }

  public static float[] getDefaultPerSideBrightness() {
    float[] brightnessPerSide = new float[6];
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
    }
    return brightnessPerSide;
  }
  
  public static IIcon[] getBlockTextures(Block block, int meta) {
    IIcon[] icons = new IIcon[6];
    int i = 0;
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      icons[i] = block.getIcon(dir.ordinal(), meta);
      i++;
    }
    return icons;
  }
  
  public static IIcon[] getBlockTextures(IBlockAccess world, int x, int y, int z) {
    Block block = world.getBlock(x, y, z);
    IIcon[] icons = new IIcon[6];
    int i = 0;
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      icons[i] = block.getIcon(world,x,y,z,dir.ordinal());
      i++;
    }
    return icons;
  }

  public static float claculateTotalBrightnessForLocation(World worldObj, int xCoord, int yCoord, int zCoord) {
    int i = worldObj.getLightBrightnessForSkyBlocks(xCoord, yCoord, zCoord, 0);
    int j = i % 65536;
    int k = i / 65536;

    float minLight = 0;
    //0.2 - 1
    float sunBrightness = worldObj.getSunBrightness(1);

    float percentRecievedFromSun = k / 255f;

    //Highest value recieved from a light
    float fromLights = j / 255f;

    // 0 - 1 for sun only, 0 - 0.6 for light only
    float recievedPercent = worldObj.getLightBrightness(xCoord, yCoord, zCoord);
    float highestValue = Math.max(fromLights, percentRecievedFromSun * sunBrightness);
    return Math.max(0.2f, highestValue);
  }

  public static float getColorMultiplierForFace(ForgeDirection face) {
    if(face == ForgeDirection.UP) {
      return 1;
    }
    if(face == ForgeDirection.DOWN) {
      return 0.5f;
    }
    if(face.offsetX != 0) {
      return 0.6f;
    }
    return 0.8f; // z
  }

  public static int setTesselatorBrightness(IBlockAccess world, int x, int y, int z) {
    Block block = world.getBlock(x, y, z);
    int res = block == null ? world.getLightBrightnessForSkyBlocks(x, y, z, 0) : block.getMixedBrightnessForBlock(world, x, y, z);
    Tessellator.instance.setBrightness(res);
    Tessellator.instance.setColorRGBA_F(1, 1, 1, 1);
    return res;
  }

  public static void renderQuad2D(double x, double y, double z, double width, double height, int colorRGB) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.setColorOpaque_I(colorRGB);
    tessellator.addVertex(x, y + height, z);
    tessellator.addVertex(x + width, y + height, z);
    tessellator.addVertex(x + width, y, z);
    tessellator.addVertex(x, y, z);
    tessellator.draw();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  public static void renderQuad2D(double x, double y, double z, double width, double height, Vector4f colorRGBA) {
    GL11.glColor4f(colorRGBA.x, colorRGBA.y, colorRGBA.z, colorRGBA.w);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.addVertex(x, y + height, z);
    tessellator.addVertex(x + width, y + height, z);
    tessellator.addVertex(x + width, y, z);
    tessellator.addVertex(x, y, z);
    tessellator.draw();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  public static Matrix4d createBillboardMatrix(TileEntity te, EntityLivingBase entityPlayer) {
    return createBillboardMatrix(new Vector3d(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5), entityPlayer);
  }

  public static Matrix4d createBillboardMatrix(Vector3d lookAt, EntityLivingBase entityPlayer) {
    Vector3d playerEye = new Vector3d(entityPlayer.posX, entityPlayer.posY + 1.62 - entityPlayer.yOffset, entityPlayer.posZ);
    Vector3d blockOrigin = new Vector3d(lookAt.x, lookAt.y, lookAt.z);
    Matrix4d lookMat = VecmathUtil.createMatrixAsLookAt(blockOrigin, playerEye, RenderUtil.UP_V);
    lookMat.setTranslation(new Vector3d());
    lookMat.invert();
    return lookMat;
  }

  public static void renderBillboard(Matrix4d lookMat, float minU, float maxU, float minV, float maxV, double size, int brightness) {
    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    tes.setBrightness(brightness);

    double s = size / 2;
    Vector3d v = new Vector3d();
    v.set(-s, s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, minU, maxV);
    v.set(s, s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, maxU, maxV);
    v.set(s, -s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, maxU, minV);
    v.set(-s, -s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, minU, minV);
    tes.draw();
  }

  /**
   * left, bottom, right, top
   * 
   * @param face
   * @return
   */
  public static List<ForgeDirection> getEdgesForFace(ForgeDirection face) {
    List<ForgeDirection> result = new ArrayList<ForgeDirection>(4);
    if(face.offsetY != 0) {
      result.add(NORTH);
      result.add(EAST);
      result.add(SOUTH);
      result.add(WEST);

    } else if(face.offsetX != 0) {
      result.add(DOWN);
      result.add(SOUTH);
      result.add(UP);
      result.add(NORTH);
    } else {
      result.add(DOWN);
      result.add(WEST);
      result.add(UP);
      result.add(EAST);
    }
    return result;
  }

  public static void addVerticesToTesselator(List<Vertex> vertices) {
    addVerticesToTessellator(vertices, Tessellator.instance);
  }

  public static void addVerticesToTessellator(List<Vertex> vertices, Tessellator tes) {
    for (Vertex v : vertices) {
      if(v.brightness != -1) {
        tes.setBrightness(v.brightness);
      }
      if(v.color != null) {
        tes.setColorRGBA_F(v.r(), v.g(), v.b(), v.a());
      }
      if(v.uv != null) {
        tes.setTextureUV(v.u(), v.v());
      }
      if(v.normal != null) {
        tes.setNormal(v.nx(), v.ny(), v.nz());
      }
      tes.addVertex(v.x(), v.y(), v.z());
    }
  }

  public static void renderConnectedTextureFace(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection face, IIcon texture, boolean forceAllEdges) {
    renderConnectedTextureFace(blockAccess, x, y, z, face, texture, forceAllEdges, true, true);
  }

  public static void renderConnectedTextureFace(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection face, IIcon texture, boolean forceAllEdges,
      boolean translateToXYZ, boolean applyFaceShading) {

    if((blockAccess == null && !forceAllEdges) || face == null || texture == null) {
      return;
    }

    if(!forceAllEdges) {
      Block block = blockAccess.getBlock(x, y, z);
      if(block == null) {
        return;
      }
      if(!block.shouldSideBeRendered(blockAccess, x + face.offsetX, y + face.offsetY, z + face.offsetZ, face.ordinal())) {
        return;
      }
    }

    BlockCoord bc = new BlockCoord(x, y, z);

    List<ForgeDirection> edges;
    if(forceAllEdges) {
      edges = RenderUtil.getEdgesForFace(face);
    } else {
      edges = RenderUtil.getNonConectedEdgesForFace(blockAccess, x, y, z, face);
    }

    Tessellator tes = Tessellator.instance;
    tes.setNormal(face.offsetX, face.offsetY, face.offsetZ);
    if(applyFaceShading) {
      float cm = RenderUtil.getColorMultiplierForFace(face);
      tes.setColorOpaque_F(cm, cm, cm);
    }

    float scaleFactor = 15f / 16f;
    Vector2f uv = new Vector2f();
    for (ForgeDirection edge : edges) {

      float xLen = 1 - Math.abs(edge.offsetX) * scaleFactor;
      float yLen = 1 - Math.abs(edge.offsetY) * scaleFactor;
      float zLen = 1 - Math.abs(edge.offsetZ) * scaleFactor;
      BoundingBox bb = BoundingBox.UNIT_CUBE.scale(xLen, yLen, zLen);

      List<Vector3f> corners = bb.getCornersForFace(face);

      for (Vector3f unitCorn : corners) {
        Vector3d corner = new Vector3d(unitCorn);
        if(translateToXYZ) {
          corner.x += x;
          corner.y += y;
          corner.z += z;
        }

        corner.x += (float) (edge.offsetX * 0.5) - Math.signum(edge.offsetX) * xLen / 2f;
        corner.y += (float) (edge.offsetY * 0.5) - Math.signum(edge.offsetY) * yLen / 2f;
        corner.z += (float) (edge.offsetZ * 0.5) - Math.signum(edge.offsetZ) * zLen / 2f;

        if(translateToXYZ) {
          RenderUtil.getUvForCorner(uv, corner, x, y, z, face, texture);
        } else {
          RenderUtil.getUvForCorner(uv, corner, 0, 0, 0, face, texture);
        }
        tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
      }

    }

  }

  public static List<ForgeDirection> getNonConectedEdgesForFace(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection face) {
    return getNonConectedEdgesForFace(blockAccess, x, y, z, face, false);
  }

  public static List<ForgeDirection> getNonConectedEdgesForFace(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection face, boolean matchMetaData) {

    Block block = blockAccess.getBlock(x, y, z);
    if(block == null) {
      return Collections.emptyList();
    }
    if(!block.shouldSideBeRendered(blockAccess, x + face.offsetX, y + face.offsetY, z + face.offsetZ, face.ordinal())) {
      return Collections.emptyList();
    }
    BlockCoord bc = new BlockCoord(x, y, z);

    List<EdgeNeighbour> edges = new ArrayList<EdgeNeighbour>(4);
    for (ForgeDirection dir : getEdgesForFace(face)) {
      edges.add(new EdgeNeighbour(bc, dir));
    }
    List<ForgeDirection> result = new ArrayList<ForgeDirection>(4);
    for (EdgeNeighbour edge : edges) {
      boolean matchingNeighbour = blockAccess.getBlock(edge.bc.x, edge.bc.y, edge.bc.z) == block;
      if(matchingNeighbour && matchMetaData) {
        matchingNeighbour = blockAccess.getBlockMetadata(x, y, z) == blockAccess.getBlockMetadata(edge.bc.x, edge.bc.y, edge.bc.z);
      }
      if(!matchingNeighbour) {
        result.add(edge.dir);
      }

    }
    return result;
  }

  public static void getUvForCorner(Vector2f uv, Vector3d corner, int x, int y, int z, ForgeDirection face, IIcon icon) {
    if(icon == null) {
      return;
    }

    Vector3d p = new Vector3d(corner);
    p.x -= x;
    p.y -= y;
    p.z -= z;

    float uWidth = 1;
    float vWidth = 1;
    if(icon != null) {
      uWidth = icon.getMaxU() - icon.getMinU();
      vWidth = icon.getMaxV() - icon.getMinV();
    }

    uv.x = (float) VecmathUtil.distanceFromPointToPlane(getUPlaneForFace(face), p);
    uv.y = (float) VecmathUtil.distanceFromPointToPlane(getVPlaneForFace(face), p);

    if(icon != null) {
      uv.x = icon.getMinU() + (uv.x * uWidth);
      uv.y = icon.getMinV() + (uv.y * vWidth);
    }

  }

  public static Vector4d getVPlaneForFace(ForgeDirection face) {
    switch (face) {
    case DOWN:
    case UP:
      return new Vector4d(0, 0, 1, 0);
    case EAST:
    case WEST:
    case NORTH:
    case SOUTH:
      return new Vector4d(0, -1, 0, 1);
    case UNKNOWN:
    default:
      break;
    }
    return null;
  }

  public static Vector4d getUPlaneForFace(ForgeDirection face) {
    switch (face) {
    case DOWN:
    case UP:
      return new Vector4d(1, 0, 0, 0);
    case EAST:
      return new Vector4d(0, 0, -1, 1);
    case WEST:
      return new Vector4d(0, 0, 1, 0);
    case NORTH:
      return new Vector4d(-1, 0, 0, 1);
    case SOUTH:
      return new Vector4d(1, 0, 0, 0);
    case UNKNOWN:
    default:
      break;
    }
    return null;
  }

  public static ForgeDirection getVDirForFace(ForgeDirection face) {
    switch (face) {
    case DOWN:
    case UP:
      return SOUTH;
    case EAST:
    case WEST:
    case NORTH:
    case SOUTH:
      return ForgeDirection.UP;
    case UNKNOWN:
    default:
      break;
    }
    return null;
  }

  public static ForgeDirection getUDirForFace(ForgeDirection face) {
    switch (face) {
    case DOWN:
    case UP:
      return ForgeDirection.EAST;
    case EAST:
      return NORTH;
    case WEST:
      return SOUTH;
    case NORTH:
      return WEST;
    case SOUTH:
      return ForgeDirection.EAST;
    case UNKNOWN:
    default:
      break;
    }
    return null;
  }

  public static void renderGuiTank(FluidTank tank, double x, double y, double zLevel, double width, double height) {
    renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
  }

  public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height) {
    if(fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
      return;
    }

    IIcon icon = fluid.getFluid().getStillIcon();
    if(icon == null) {
      icon = fluid.getFluid().getIcon();
      if(icon == null) {
        return;
      }
    }

    double fullness = (double) amount / (double) capacity;
    int fluidHeight = (int) Math.round(height * fullness);

    RenderUtil.bindBlockTexture();
    y = y + (47 - fluidHeight);
    GL11.glColor4f(1, 1, 1, 0.75f);
    GL11.glEnable(GL11.GL_BLEND);
    drawTexturedModelRectFromIcon(x, y, zLevel, icon, width, fluidHeight);
    GL11.glDisable(GL11.GL_BLEND);
  }

  public static void drawTexturedModelRectFromIcon(double x, double y, double z, IIcon icon, double width, double height) {
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.addVertexWithUV(x, y + height, z, icon.getMinU(), icon.getMaxV());
    tessellator.addVertexWithUV(x + width, y + height, z, icon.getMaxU(), icon.getMaxV());
    tessellator.addVertexWithUV(x + width, y, z, icon.getMaxU(), icon.getMinV());
    tessellator.addVertexWithUV(x, y, z, icon.getMinU(), icon.getMinV());
    tessellator.draw();
  }

  private static class EdgeNeighbour {
    final ForgeDirection dir;
    final BlockCoord bc;

    public EdgeNeighbour(BlockCoord bc, ForgeDirection dir) {
      this.dir = dir;
      this.bc = bc.getLocation(dir);
    }

  }

}
