package austeretony.oxygen_exchange.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ExchangeMenuContainer extends Container {

    private final EntityPlayer player;

    private final IInventory playerInventory, exchangeInventory;

    public ExchangeMenuContainer(EntityPlayer player, IInventory playerInventory) {
        this.player = player;
        this.playerInventory = playerInventory; 
        this.exchangeInventory = new ExchangeMenuInventory();
        int j;                  
        for (j = 0; j < 5; j++)//'preview' slots (other player offer)   
            this.addSlotToContainer(new ExchangeSlot(this.exchangeInventory, j, 0, 0, true));
        for (j = 5; j < 10; j++)//client offer slots      
            this.addSlotToContainer(new ExchangeSlot(this.exchangeInventory, j, 0, 0));
        for (j = 9; j < 36; j++)//client inventory slots      
            this.addSlotToContainer(new Slot(this.playerInventory, j, 0, 0));
        for (j = 0; j < 9; j++)//client hotbar slots      
            this.addSlotToContainer(new Slot(this.playerInventory, j, 0, 0));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack returningItemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())  {
            ItemStack slotItemStack = slot.getStack();
            returningItemStack = slotItemStack.copy();

            if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(slotItemStack, 37, 46, false))
                    return ItemStack.EMPTY;
            } else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(slotItemStack, 10, 37, false))
                    return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(slotItemStack, 10, 37, false))
                return ItemStack.EMPTY;

            if (slotItemStack.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if (slotItemStack.getCount() == returningItemStack.getCount())
                return ItemStack.EMPTY;
        }

        return returningItemStack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        for (int i = 5; i < 10; i++)
            player.inventory.addItemStackToInventory(this.exchangeInventory.getStackInSlot(i));
    }

    public void disableClientOfferSlots() {
        for (int i = 5; i < 10; i++)
            ((ExchangeSlot) this.inventorySlots.get(i)).setDisabled(true);
    }

    public void enableClientOfferSlots() {
        for (int i = 5; i < 10; i++)
            ((ExchangeSlot) this.inventorySlots.get(i)).setDisabled(false);
    }

    public IInventory getExchangeInventory() {
        return this.exchangeInventory;
    }
}
