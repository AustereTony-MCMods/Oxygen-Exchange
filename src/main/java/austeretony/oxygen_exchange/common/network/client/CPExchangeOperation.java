package austeretony.oxygen_exchange.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPExchangeOperation extends Packet {

    private int ordinal;

    public CPExchangeOperation() {}

    public CPExchangeOperation(EnumOperation command) {
        this.ordinal = command.ordinal();
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final int ordinal = buffer.readByte();
        switch (EnumOperation.values()[ordinal]) {
        case OFFERED:
            OxygenHelperClient.addRoutineTask(()->ExchangeManagerClient.instance().getExchangeMenuManager().madeOffer());
            break;
        case CANCELED:
            OxygenHelperClient.addRoutineTask(()->ExchangeManagerClient.instance().getExchangeMenuManager().canceledExchange());
            break;        
        case CONFIRMED:
            OxygenHelperClient.addRoutineTask(()->ExchangeManagerClient.instance().getExchangeMenuManager().confirmedExchange());
            break;
        case OTHER_CANCELED:
            OxygenHelperClient.addRoutineTask(()->ExchangeManagerClient.instance().getExchangeMenuManager().otherPlayerCanceledExchange());
            break;
        case OTHER_CONFIRMED:
            OxygenHelperClient.addRoutineTask(()->ExchangeManagerClient.instance().getExchangeMenuManager().otherPlayerConfirmedExchange());
            break;
        }
    }

    public enum EnumOperation {

        OFFERED,
        CANCELED,
        CONFIRMED,
        OTHER_CANCELED,
        OTHER_CONFIRMED
    }
}
