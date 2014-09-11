package crazypants.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class IconToggleButton extends IconButton {

  private boolean selected = false;

  public IconToggleButton(FontRenderer fr, int id, int x, int y, IIcon icon, ResourceLocation texture) {
    super(fr, id, x, y, icon, texture);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  public int getHoverState(boolean par1) {
    int result = 1;
    if(!enabled || selected) {
      result = 0;
    } else if(par1) {
      result = 2;
    }
    return result;
  }
}
