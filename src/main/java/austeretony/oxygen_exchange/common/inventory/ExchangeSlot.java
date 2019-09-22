package austeretony.oxygen_exchange.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ExchangeSlot extends Slot {

    private boolean disabled;

    public ExchangeSlot(IInventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
    }

    public ExchangeSlot(IInventory inventory, int index, int xPosition, int yPosition, boolean disabled) {
        this(inventory, index, xPosition, yPosition);
        this.disabled = disabled;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return !this.disabled;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return !this.disabled;
    }

    public void setDisabled(boolean flag) {
        this.disabled = flag;
    }
}
