package austeretony.oxygen_exchange.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import austeretony.oxygen_exchange.server.ExchangeManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPSendExchangeRequest extends Packet {

    private int index;

    public SPSendExchangeRequest() {}

    public SPSendExchangeRequest(int index) {
        this.index = index;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeInt(this.index);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), ExchangeMain.EXCHANGE_OPERATION_REQUEST_ID)) {
            final int index = buffer.readInt();
            OxygenHelperServer.addRoutineTask(()->ExchangeManagerServer.instance().getExchangeProcessesManager().sendExchangeRequest(playerMP, index));
        }
    }
}
