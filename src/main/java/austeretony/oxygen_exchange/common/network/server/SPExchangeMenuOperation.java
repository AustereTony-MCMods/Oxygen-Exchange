package austeretony.oxygen_exchange.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_exchange.common.EnumExchangeOperation;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import austeretony.oxygen_exchange.server.ExchangeManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPExchangeMenuOperation extends Packet {

    private int ordinal;

    private long offeredCurrency;

    public SPExchangeMenuOperation() {}

    public SPExchangeMenuOperation(EnumExchangeOperation operation, long offeredCurrency) {
        this.ordinal = operation.ordinal();
        this.offeredCurrency = offeredCurrency;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
        buffer.writeLong(this.offeredCurrency);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), ExchangeMain.EXCHANGE_OPERATION_REQUEST_ID)) {
            final int ordinal = buffer.readByte();
            final long offeredCurrency = buffer.readLong();
            if (ordinal >= 0 && ordinal < EnumExchangeOperation.values().length)
                OxygenHelperServer.addRoutineTask(()->ExchangeManagerServer.instance().getExchangeProcessesManager()
                        .processExchangeOperation(playerMP, EnumExchangeOperation.values()[ordinal], offeredCurrency));
        }
    }
}
