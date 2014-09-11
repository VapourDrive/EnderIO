package crazypants.enderio.conduit;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gas.GasUtil;

public enum ConduitDisplayMode {
  ALL,
  POWER,
  REDSTONE,
  FLUID,
  ITEM,
  GAS;

  public static ConduitDisplayMode next(ConduitDisplayMode mode) {
    int index = mode.ordinal() + 1;
    if(index >= values().length) {
      index = 0;
    }
    ConduitDisplayMode res = values()[index];
    if(res == GAS && !GasUtil.isGasConduitEnabled()) {
      return next(res);
    }
    return res;
  }

  public static ConduitDisplayMode previous(ConduitDisplayMode mode) {
    int index = mode.ordinal() - 1;
    if(index < 0) {
      index = values().length - 1;
    }
    ConduitDisplayMode res = values()[index];
    if(res == GAS && !GasUtil.isGasConduitEnabled()) {
      return previous(res);
    }
    return res;
  }

  public static ConduitDisplayMode getDisplayMode(ItemStack equipped) {
    if(equipped == null || equipped.getItem() != EnderIO.itemYetaWench) {
      return ALL;
    }
    int index = equipped.getItemDamage();
    index = MathHelper.clamp_int(index, 0, ConduitDisplayMode.values().length - 1);
    return ConduitDisplayMode.values()[index];
  }

  public static void setDisplayMode(ItemStack equipped, ConduitDisplayMode mode) {
    if(mode == null || equipped == null) {
      return;
    }
    equipped.setItemDamage(mode.ordinal());
  }

  public ConduitDisplayMode next() {
    return next(this);
  }

  public ConduitDisplayMode previous() {
    return previous(this);
  }

}