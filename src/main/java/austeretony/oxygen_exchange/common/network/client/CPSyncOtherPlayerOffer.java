package austeretony.oxygen_exchange.common.network.client;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncOtherPlayerOffer extends ProxyPacket {

    private int offeredCurrency;

    private ItemStack[] offeredItems;

    public CPSyncOtherPlayerOffer() {}

    public CPSyncOtherPlayerOffer(int offeredCurrency, ItemStack[] offeredItems) {
        this.offeredCurrency = offeredCurrency;
        this.offeredItems = offeredItems;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeInt(this.offeredCurrency);
        for (ItemStack itemStack : this.offeredItems)
            PacketBufferUtils.writeItemStack(itemStack, buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.offeredCurrency = buffer.readInt();
        this.offeredItems = new ItemStack[5];
        for (int i = 0; i < 5; i++)
            this.offeredItems[i] = PacketBufferUtils.readItemStack(buffer);
        ExchangeManagerClient.instance().setOtherPlayerOffer(this.offeredCurrency, this.offeredItems);
    }
}
