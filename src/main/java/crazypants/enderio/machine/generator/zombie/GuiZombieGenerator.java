package crazypants.enderio.machine.generator.zombie;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiZombieGenerator extends GuiMachineBase {

  private TileZombieGenerator gen;


  public GuiZombieGenerator(InventoryPlayer inventory, TileZombieGenerator tileEntity) {
    super(tileEntity, new ContainerZombieGenerator(inventory, tileEntity));
    gen = tileEntity;
    
    addToolTip(new GuiToolTip(new Rectangle(80, 21, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("zombieGenerator.fuelTank");        
        text.add(heading);
        text.add(Fluids.toCapactityString(gen.fuelTank));
      }

    });
    
  }
  
  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    if(mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 78;
      int y = 19;
      int w = 15 + 4;
      int h = 47 + 4;
      renderSlotHighlight(PULL_COLOR,x,y,w,h);     
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/zombieGenerator.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    int scaled;

    FontRenderer fr = getFontRenderer();
    double output = 0;
    if(gen.isActive()) {
      output = gen.outputPerTick;
    }
    String txt =  Lang.localize("combustionGenerator.output") + " " + PowerDisplayUtil.formatPower(output) + " " + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr();
    int sw = fr.getStringWidth(txt);
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, guiTop + fr.FONT_HEIGHT / 2 + 3, ColorUtil.getRGB(Color.WHITE));

    int x = guiLeft + 80;
    int y = guiTop + 21;    
    if(gen.fuelTank.getFluidAmount() > 0) {
    
      RenderUtil.renderGuiTank(gen.fuelTank.getFluid(), gen.fuelTank.getCapacity(), gen.fuelTank.getFluidAmount(), x, y, zLevel, 15, 47);

      if(gen.isActive()) {
        txt = gen.tickPerBucketOfFuel / 1000 + " t/Mb";
        sw = fr.getStringWidth(txt);
        fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 46, ColorUtil.getRGB(Color.WHITE));
      }      
    }

    RenderUtil.bindTexture("enderio:textures/gui/zombieGenerator.png");
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected int getPowerX() {
    return 15;
  }

  @Override
  protected int getPowerY() {
    return 20;
  }

  @Override
  protected int getPowerHeight() {
    return 48;
  }

}
