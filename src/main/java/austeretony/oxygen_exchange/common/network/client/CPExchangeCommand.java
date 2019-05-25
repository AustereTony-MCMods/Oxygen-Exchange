package austeretony.oxygen_exchange.common.network.client;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPExchangeCommand extends ProxyPacket {

    private EnumCommand command;

    public CPExchangeCommand() {}

    public CPExchangeCommand(EnumCommand command) {
        this.command = command;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.command.ordinal());
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.command = EnumCommand.values()[buffer.readByte()];
        switch (this.command) {
        case SET_OTHER_RESET_OFFER:
            ExchangeManagerClient.instance().resetOtherPlayerOffer();
            break;
        case SET_OTHER_CONFIRMED_EXCHANGE:
            ExchangeManagerClient.instance().setOtherPlayerConfirmedExchange();
            break;
        }
    }

    public enum EnumCommand {

        SET_OTHER_RESET_OFFER,
        SET_OTHER_CONFIRMED_EXCHANGE
    }
}
