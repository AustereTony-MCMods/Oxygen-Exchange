package austeretony.oxygen_exchange.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_exchange.common.ExchangeManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPSendExchangeRequest extends ProxyPacket {

    private int index;

    public SPSendExchangeRequest() {}

    public SPSendExchangeRequest(int index) {
        this.index = index;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeInt(this.index);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        ExchangeManagerServer.instance().sendExchangeRequest(getEntityPlayerMP(netHandler), buffer.readInt());
    }
}
