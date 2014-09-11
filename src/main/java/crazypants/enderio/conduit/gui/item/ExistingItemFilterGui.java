package crazypants.enderio.conduit.gui.item;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.ExistingItemFilter;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.gui.IGuiOverlay;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.IGuiScreen;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;
import crazypants.vecmath.Vector4f;

public class ExistingItemFilterGui implements IItemFilterGui {

  private static final int ID_NBT = GuiExternalConnection.nextButtonId();
  private static final int ID_META = GuiExternalConnection.nextButtonId();
  private static final int ID_ORE_DICT = GuiExternalConnection.nextButtonId();
  private static final int ID_STICKY = GuiExternalConnection.nextButtonId();
  
  private static final int ID_SNAPSHOT = GuiExternalConnection.nextButtonId();
  private static final int ID_CLEAR = GuiExternalConnection.nextButtonId();
  private static final int ID_SHOW = GuiExternalConnection.nextButtonId();
  

  private IItemConduit itemConduit;
  private GuiExternalConnection gui;

  private ToggleButtonEIO useMetaB;
  private ToggleButtonEIO useNbtB;
  private ToggleButtonEIO useOreDictB;
  private ToggleButtonEIO stickyB;
  
  private GuiButton snapshotB;
  private GuiButton clearB;
  private GuiButton showB;
  private SnapshotOverlay snapshotOverlay;

  boolean isInput;

  private ExistingItemFilter filter;

  public ExistingItemFilterGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    this.gui = gui;
    this.itemConduit = itemConduit;
    this.isInput = isInput;

    if(isInput) {
      filter = (ExistingItemFilter) itemConduit.getInputFilter(gui.getDir());
    } else {
      filter = (ExistingItemFilter) itemConduit.getOutputFilter(gui.getDir());
    }

    int butLeft = 37;
    int x = butLeft;
    int y = 68;

    useMetaB = new ToggleButtonEIO(gui, ID_META, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    useMetaB.setSelectedToolTip(Lang.localize("gui.conduit.item.matchMetaData"));
    useMetaB.setUnselectedToolTip(Lang.localize("gui.conduit.item.ignoreMetaData"));
    useMetaB.setPaintSelectedBorder(false);

    x += 20;
    stickyB = new ToggleButtonEIO(gui, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    String[] lines = Lang.localizeList("gui.conduit.item.stickyEnabled");
    stickyB.setSelectedToolTip(lines);
    stickyB.setUnselectedToolTip(Lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = butLeft;

    x += 20;
    useNbtB = new ToggleButtonEIO(gui, ID_NBT, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
    useNbtB.setSelectedToolTip(Lang.localize("gui.conduit.item.matchNBT"));
    useNbtB.setUnselectedToolTip(Lang.localize("gui.conduit.item.ignoreNBT"));
    useNbtB.setPaintSelectedBorder(false);

    x = butLeft;
    useOreDictB = new ToggleButtonEIO(gui, ID_ORE_DICT, x, y, IconEIO.FILTER_ORE_DICT_OFF, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setSelectedToolTip(Lang.localize("gui.conduit.item.oreDicEnabled"));
    useOreDictB.setUnselectedToolTip(Lang.localize("gui.conduit.item.oreDicDisabled"));
    useOreDictB.setPaintSelectedBorder(false);
    
    snapshotOverlay = new SnapshotOverlay();
    gui.addOverlay(snapshotOverlay);
    
    
  }
  
  @Override
  public void mouseClicked(int x, int y, int par3) {      
  }

  @Override
  public void updateButtons() {

    ExistingItemFilter activeFilter = filter;

    useNbtB.onGuiInit();
    useNbtB.setSelected(activeFilter.isMatchNBT());

    useOreDictB.onGuiInit();
    useOreDictB.setSelected(activeFilter.isUseOreDict());

    if(!isInput) {
      stickyB.onGuiInit();
      stickyB.setSelected(activeFilter.isSticky());
    }

    useMetaB.onGuiInit();
    useMetaB.setSelected(activeFilter.isMatchMeta());
    
    int x = gui.getGuiLeft() + 80;
    int y = gui.getGuiTop() + 65;
    snapshotB = new GuiButton(ID_SNAPSHOT, x, y, 60, 20, "Snapshot");
    
    x += 65;
    showB = new GuiButton(ID_SHOW, x, y, 40, 20, "Show");
    
    x = gui.getGuiLeft() + 80;
    y += 22;
    clearB = new GuiButton(ID_CLEAR, x, y, 60, 20, "Clear");
    
    clearB.enabled = filter.getSnapshot() != null;         
    showB.enabled = clearB.enabled;
    
    gui.addButton(snapshotB);
    gui.addButton(clearB);
    gui.addButton(showB);

  }

  public void actionPerformed(GuiButton guiButton) {

    if(guiButton.id == ID_META) {
      filter.setMatchMeta(useMetaB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_NBT) {
      filter.setMatchNBT(useNbtB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_STICKY) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_ORE_DICT) {
      filter.setUseOreDict(useOreDictB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_SNAPSHOT) {
      sendSnapshotPacket(false);
    } else if(guiButton.id == ID_CLEAR) {
      sendSnapshotPacket(true);
    } else if(guiButton.id == ID_SHOW) {
      showSnapshotOverlay();  
    }
  }

  private void showSnapshotOverlay() {
    snapshotOverlay.setVisible(true);    
  }

  private void sendSnapshotPacket(boolean isClear) {    
    PacketHandler.INSTANCE.sendToServer(new PacketExistingItemFilterSnapshot(itemConduit, gui.getDir(),isInput,isClear));
  }

  private void sendFilterChange() {
    updateButtons();
    PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));    
  }

  public void deactivate() {
    useNbtB.detach();
    useMetaB.detach();
    useOreDictB.detach();    
    stickyB.detach();
    gui.removeButton(snapshotB);
    gui.removeButton(clearB);
    gui.removeButton(showB);
  }

  public void renderCustomOptions(int top, float par1, int par2, int par3) {
//    GL11.glColor3f(1, 1, 1);
//    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
//    gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 68, 0, 238, 18 * 5, 18);
//    if(filter.isAdvanced()) {
//      gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 86, 0, 238, 18 * 5, 18);
//    }
  }
  
  class SnapshotOverlay implements IGuiOverlay {

    boolean visible;
    
    @Override
    public void init(IGuiScreen screen) {           
    }

    @Override
    public Rectangle getBounds() {     
      return new Rectangle(0,0,gui.width,gui.height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTick) {      
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glEnable(GL11.GL_BLEND);
      RenderUtil.renderQuad2D(4, 4, 0, gui.getXSize() - 9, gui.getYSize() - 8, new Vector4f(0,0,0,1));
      RenderUtil.renderQuad2D(6, 6, 0, gui.getXSize() - 13, gui.getYSize() - 12, new Vector4f(0.6,0.6,0.6,1));
      
      Minecraft mc = Minecraft.getMinecraft();
      RenderItem itemRenderer = new RenderItem();
            
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      
      List<ItemStack> snapshot = filter.getSnapshot();
      int x = 15;
      int y = 10;
      int count = 0;
      for(ItemStack st : snapshot) {
        if(st != null) {
          itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), st, x, y);
          //itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), st, x, y, s);  
        }        
        x += 20;
        count++;
        if(count % 9 == 0) {
          x = 15;
          y += 20;
        }
      }            
    }

    @Override
    public void setVisible(boolean visible) {
      this.visible = visible;      
    }

    @Override
    public boolean isVisible() {
      return visible;
    }

    @Override
    public boolean handleMouseInput(int x, int y, int b) {
      return true;
    }

    @Override
    public boolean isMouseInBounds(int mouseX, int mouseY) {      
      return getBounds().contains(mouseX, mouseY);
    }
    
  }
}
