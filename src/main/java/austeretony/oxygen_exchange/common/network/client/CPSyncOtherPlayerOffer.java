package austeretony.oxygen_exchange.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncOtherPlayerOffer extends Packet {

    private long offeredCurrency;

    private byte[] compressedItems;

    public CPSyncOtherPlayerOffer() {}

    public CPSyncOtherPlayerOffer(long offeredCurrency, byte[] compressedItems) {
        this.offeredCurrency = offeredCurrency;
        this.compressedItems = compressedItems;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.offeredCurrency);
        buffer.writeShort(this.compressedItems.length);
        buffer.writeBytes(this.compressedItems);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final long offeredCurrency = buffer.readLong();
        final byte[] compressedItems = new byte[buffer.readShort()];
        buffer.readBytes(compressedItems);
        OxygenHelperClient.addRoutineTask(()->ExchangeManagerClient.instance().getExchangeMenuManager().otherPlayerMadeOffer(offeredCurrency, compressedItems));
    }
}
