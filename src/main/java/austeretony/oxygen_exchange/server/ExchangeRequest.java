package austeretony.oxygen_exchange.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.notification.AbstractNotification;
import austeretony.oxygen_core.common.notification.EnumNotification;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_exchange.common.config.ExchangeConfig;
import austeretony.oxygen_exchange.common.main.EnumExchangeStatusMessage;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ExchangeRequest extends AbstractNotification {

    public final UUID senderUUID;

    public final String senderUsername;

    public ExchangeRequest(UUID senderUUID, String senderUsername) {
        this.senderUUID = senderUUID;
        this.senderUsername = senderUsername;
    }

    @Override
    public EnumNotification getType() {
        return EnumNotification.REQUEST;
    }

    @Override
    public String getDescription() {
        return "oxygen_exchange.request.exchange";
    }

    @Override
    public String[] getArguments() {
        return new String[] {this.senderUsername};
    }

    @Override
    public int getIndex() {
        return ExchangeMain.EXCHANGE_REQUEST_ID;
    }

    @Override
    public int getExpireTimeSeconds() {
        return ExchangeConfig.EXCHANGE_REQUEST_EXPIRE_TIME_SECONDS.asInt();
    }

    @Override
    public void process() {}

    @Override
    public void accepted(EntityPlayer player) {        
        if (OxygenHelperServer.isPlayerOnline(this.senderUUID)) {
            ExchangeManagerServer.instance().getExchangeProcessesManager().processExchangeRequestReply(player, this.senderUUID);
            OxygenHelperServer.sendStatusMessage(CommonReference.playerByUUID(this.senderUUID), ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeStatusMessage.EXCHANGE_REQUEST_ACCEPTED_SENDER.ordinal());
        }
        OxygenHelperServer.sendStatusMessage((EntityPlayerMP) player, ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeStatusMessage.EXCHANGE_REQUEST_ACCEPTED_TARGET.ordinal());
    }

    @Override
    public void rejected(EntityPlayer player) {
        if (OxygenHelperServer.isPlayerOnline(this.senderUUID))
            OxygenHelperServer.sendStatusMessage(CommonReference.playerByUUID(this.senderUUID), ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeStatusMessage.EXCHANGE_REQUEST_REJECTED_SENDER.ordinal());
        OxygenHelperServer.sendStatusMessage((EntityPlayerMP) player, ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeStatusMessage.EXCHANGE_REQUEST_REJECTED_TARGET.ordinal());
    }

    @Override
    public void expired() {}
}
