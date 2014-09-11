package crazypants.enderio.machine;

import java.awt.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractMachineContainer extends Container {

  protected final AbstractMachineEntity tileEntity;

  public AbstractMachineContainer(InventoryPlayer playerInv, AbstractMachineEntity te) {
    this.tileEntity = te;

    addMachineSlots(playerInv);

    if(te.getSlotDefinition().getNumUpgradeSlots() == 1) {
      addSlotToContainer(new Slot(te, te.getSlotDefinition().getMinUpgradeSlot(), getUpgradeOffset().x, getUpgradeOffset().y) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(ItemStack itemStack) {
          return tileEntity.isItemValidForSlot(tileEntity.getSlotDefinition().getMinUpgradeSlot(), itemStack);
        }
      });
    }

    int x = getPlayerInventoryOffset().x;
    int y = getPlayerInventoryOffset().y;
    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, x + j * 18, y + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, x + i * 18, y + 58));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return tileEntity.isUseableByPlayer(entityplayer);
  }

  protected Point getPlayerInventoryOffset() {
    return new Point(8,84);
  }
  
  protected Point getUpgradeOffset() {
    return new Point(12,60);
  }
  
  protected abstract void addMachineSlots(InventoryPlayer playerInv);

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {

    SlotDefinition slotDef = tileEntity.getSlotDefinition();

    int startPlayerSlot = slotDef.getNumSlots();
    int endPlayerSlot = startPlayerSlot + 26;
    int startHotBarSlot = endPlayerSlot + 1;
    int endHotBarSlot = startHotBarSlot + 9;

    ItemStack copystack = null;
    Slot slot = (Slot) inventorySlots.get(slotIndex);
    if(slot != null && slot.getHasStack()) {

      ItemStack origStack = slot.getStack();
      copystack = origStack.copy();

      if(slotDef.isInputSlot(slotIndex) || slotDef.isUpgradeSlot(slotIndex)) {
        // merge from machine input slots to inventory
        if(!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, false)) {
          return null;
        }

      } else if(slotDef.isOutputSlot(slotIndex)) {
        // merge result
        if(!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, true)) {
          return null;
        }
        slot.onSlotChange(origStack, copystack);

      } else {
        //Check from inv->input then inv->upgrade then inv->hotbar or hotbar->inv
        if(slotIndex >= startPlayerSlot) {
          if(slotDef.getNumInputSlots() <= 0 || !mergeItemStack(origStack, slotDef.getMinInputSlot(), slotDef.getMaxInputSlot() + 1, false)) {
            if(slotDef.getNumUpgradeSlots() <= 0 || !mergeItemStack(origStack, slotDef.getMinUpgradeSlot(), slotDef.getMaxUpgradeSlot() + 1, false)) {
              if(slotIndex <= endPlayerSlot) {
                if(!mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false)) {
                  return null;
                }
              } else if(slotIndex >= startHotBarSlot && slotIndex <= endHotBarSlot) {
                if(!mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
                  return null;
                }
              }
            }
          }
        }
      }

      if(origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      slot.onSlotChanged();

      if(origStack.stackSize == copystack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return copystack;
  }

  /**
   * Added validation of slot input
   */
  @Override
  protected boolean mergeItemStack(ItemStack par1ItemStack, int fromIndex, int toIndex, boolean reversOrder) {

    boolean result = false;
    int checkIndex = fromIndex;

    if(reversOrder) {
      checkIndex = toIndex - 1;
    }

    Slot slot;
    ItemStack itemstack1;

    if(par1ItemStack.isStackable()) {

      while (par1ItemStack.stackSize > 0 && (!reversOrder && checkIndex < toIndex || reversOrder && checkIndex >= fromIndex)) {
        slot = (Slot) this.inventorySlots.get(checkIndex);
        itemstack1 = slot.getStack();

        if(itemstack1 != null && itemstack1.getItem() == par1ItemStack.getItem()
            && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage())
            && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1)
            && slot.isItemValid(par1ItemStack)) {

          int mergedSize = itemstack1.stackSize + par1ItemStack.stackSize;
          int maxStackSize =  Math.min(par1ItemStack.getMaxStackSize(), slot.getSlotStackLimit());
          if(mergedSize <= maxStackSize) {
            par1ItemStack.stackSize = 0;
            itemstack1.stackSize = mergedSize;
            slot.onSlotChanged();
            result = true;
          } else if(itemstack1.stackSize < maxStackSize) {
            par1ItemStack.stackSize -= maxStackSize - itemstack1.stackSize;
            itemstack1.stackSize = maxStackSize;
            slot.onSlotChanged();
            result = true;
          }
        }

        if(reversOrder) {
          --checkIndex;
        } else {
          ++checkIndex;
        }
      }
    }

    if(par1ItemStack.stackSize > 0) {
      if(reversOrder) {
        checkIndex = toIndex - 1;
      } else {
        checkIndex = fromIndex;
      }

      while (!reversOrder && checkIndex < toIndex || reversOrder && checkIndex >= fromIndex) {
        slot = (Slot) this.inventorySlots.get(checkIndex);
        itemstack1 = slot.getStack();

        if(itemstack1 == null && slot.isItemValid(par1ItemStack)) {
          ItemStack in = par1ItemStack.copy();
          in.stackSize = Math.min(in.stackSize, slot.getSlotStackLimit());

          slot.putStack(in);
          slot.onSlotChanged();
          if(in.stackSize >= par1ItemStack.stackSize) {
            par1ItemStack.stackSize = 0;
          } else {
            par1ItemStack.stackSize -= in.stackSize;
          }
          result = true;
          break;
        }

        if(reversOrder) {
          --checkIndex;
        } else {
          ++checkIndex;
        }
      }
    }

    return result;
  }

}
