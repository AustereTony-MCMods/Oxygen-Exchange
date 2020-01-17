package austeretony.oxygen_exchange.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class InventoryExchangeMenu implements IInventory {

    public final NonNullList<ItemStack> inventoryArray = NonNullList.<ItemStack>withSize(10, ItemStack.EMPTY);

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getSizeInventory() {
        return this.inventoryArray.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventoryArray)
            if (!itemStack.isEmpty())
                return false;
        return true;    
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.inventoryArray.get(index) != ItemStack.EMPTY ? this.inventoryArray.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.inventoryArray, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!this.inventoryArray.get(index).isEmpty()) {
            ItemStack itemStack = this.inventoryArray.get(index);
            this.inventoryArray.set(index, ItemStack.EMPTY);
            return itemStack;              
        } else   
            return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < this.inventoryArray.size())
            this.inventoryArray.set(index, stack);
        if (!stack.isEmpty() 
                && stack.getCount() > this.getInventoryStackLimit())
            stack.setCount(this.getInventoryStackLimit());
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.inventoryArray.clear();
    }
}
