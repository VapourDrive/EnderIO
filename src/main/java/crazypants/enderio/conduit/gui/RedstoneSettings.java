package crazypants.enderio.conduit.gui;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.packet.PacketRedstoneConduitSignalColor;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.gui.ColorButton;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.network.PacketHandler;
import crazypants.render.ColorUtil;
import crazypants.util.DyeColor;
import crazypants.util.Lang;

public class RedstoneSettings extends BaseSettingsPanel {

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private ColorButton cb;

  private String signalColorStr = Lang.localize("gui.conduit.redstone.color");
  private IInsulatedRedstoneConduit insCon;

  public RedstoneSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_REDSTONE, Lang.localize("itemRedstoneConduitInsulated.name"), gui, con);

    int x = gap + gui.getFontRenderer().getStringWidth(signalColorStr) + gap + 2;
    int y = customTop;
    cb = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    cb.setToolTipHeading(Lang.localize("gui.conduit.redstone.signalColor"));
    if(con instanceof IInsulatedRedstoneConduit) {
      insCon = (IInsulatedRedstoneConduit) con;
      DyeColor sigCol = insCon.getSignalColor(gui.getDir());
      cb.setColorIndex(sigCol.ordinal());
    }
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if(guiButton.id == ID_COLOR_BUTTON) {
      insCon.setSignalColor(gui.getDir(), DyeColor.values()[cb.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitSignalColor(insCon, gui.getDir()));
    }
  }

  @Override
  protected void initCustomOptions() {
    if(insCon != null) {
      cb.setColorIndex(cb.getColorIndex());
      cb.onGuiInit();
    }
  }

  @Override
  public void deactivate() {
    super.deactivate();
    cb.setToolTip((String[]) null);
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    if(insCon != null) {
      gui.getFontRenderer().drawString(signalColorStr, left, top, ColorUtil.getRGB(Color.darkGray));
    }
  }

}
