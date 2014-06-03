package crazypants.enderio.conduit.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.IItemFilter;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.gui.ColorButton;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.render.RenderUtil;
import crazypants.util.DyeColor;
import crazypants.util.Lang;

public class BasicItemFilterGui {
    
  private static final int ID_WHITELIST = 17;
  private static final int ID_NBT = 18;
  private static final int ID_META = 19;
  private static final int ID_ORE_DICT = 20;
  private static final int ID_STICKY = 21;    
  
  private IItemConduit itemConduit;
  private GuiExternalConnection gui;
  
  private ToggleButtonEIO useMetaB;
  private ToggleButtonEIO useNbtB;
  private IconButtonEIO whiteListB;
  private ToggleButtonEIO useOreDictB;
  private ToggleButtonEIO stickyB;
    
  boolean isAdvanced;
  boolean isInput;
  
  private ItemFilter filter;
  
  public BasicItemFilterGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    this.gui = gui;
    this.itemConduit = itemConduit;
    this.isInput = isInput;
    
    if(isInput) {
      filter = (ItemFilter) itemConduit.getInputFilter(gui.dir);
    } else {
      filter = (ItemFilter) itemConduit.getOutputFilter(gui.dir);
    }
    isAdvanced = filter.isAdvanced();
    
    int butLeft = 124;
    int x = butLeft ;
    int y = 68;
    whiteListB = new IconButtonEIO(gui, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(Lang.localize("gui.conduit.item.whitelist"));

    x += 20;
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
  }
  
  public void updateButtons() {
    
    ItemFilter activeFilter = (ItemFilter)filter;
    
    if(isAdvanced) {
      useNbtB.onGuiInit();
      useNbtB.setSelected(activeFilter.isMatchNBT());

      useOreDictB.onGuiInit();
      useOreDictB.setSelected(activeFilter.isUseOreDict());
      
      if(!isInput) {
        stickyB.onGuiInit();
        stickyB.setSelected(activeFilter.isSticky());
      }
    }

    useMetaB.onGuiInit();
    useMetaB.setSelected(activeFilter.isMatchMeta());

    whiteListB.onGuiInit();
    if(activeFilter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(Lang.localize("gui.conduit.item.blacklist"));
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(Lang.localize("gui.conduit.item.whitelist"));
    }
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
    } else if(guiButton.id == ID_WHITELIST) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    } 
  }
  
  private void sendFilterChange() {
    updateButtons();
    EnderIO.packetPipeline.sendToServer(new PacketItemConduitFilter(itemConduit, gui.dir));
  }
  
  public void deactivate() {        
    useNbtB.detach();
    useMetaB.detach();
    useOreDictB.detach();
    whiteListB.detach();
    stickyB.detach();
  }
  
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    GL11.glColor3f(1, 1, 1);
    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
    gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 68, 0, 238, 18 * 5, 18);
    if(filter.isAdvanced()) {      
      gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 86, 0, 238, 18 * 5, 18);
    }
  }
  
}
