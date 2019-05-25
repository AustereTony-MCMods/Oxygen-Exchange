package austeretony.oxygen_exchange.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_exchange.common.ExchangeManagerServer;
import austeretony.oxygen_exchange.common.main.ExchangeProcess.EnumExchangeOperation;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPExchangeMenuOperation extends ProxyPacket {

    private EnumExchangeOperation operation;

    private int offeredCurrency;

    public SPExchangeMenuOperation() {}

    public SPExchangeMenuOperation(EnumExchangeOperation operation) {
        this.operation = operation;
    }

    public SPExchangeMenuOperation(int offeredCurrency) {
        this.operation = EnumExchangeOperation.OFFER;
        this.offeredCurrency = offeredCurrency;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.operation.ordinal());
        if (this.operation == EnumExchangeOperation.OFFER)
            buffer.writeInt(this.offeredCurrency);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.operation = EnumExchangeOperation.values()[buffer.readByte()];
        ExchangeManagerServer.instance().processExchangeOperation(getEntityPlayerMP(netHandler), this.operation, this.operation == EnumExchangeOperation.OFFER ? buffer.readInt() : 0);
    }
}
