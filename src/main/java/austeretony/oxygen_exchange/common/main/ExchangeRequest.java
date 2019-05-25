package austeretony.oxygen_exchange.common.main;

import java.util.UUID;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.notification.AbstractNotification;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.notification.EnumNotifications;
import austeretony.oxygen_exchange.common.ExchangeManagerServer;
import austeretony.oxygen_exchange.common.config.ExchangeConfig;
import net.minecraft.entity.player.EntityPlayer;

public class ExchangeRequest extends AbstractNotification {

    public final int index;

    public final UUID senderUUID;

    public final String senderUsername;

    public ExchangeRequest(int index, UUID senderUUID, String senderUsername) {
        this.index = index;
        this.senderUUID = senderUUID;
        this.senderUsername = senderUsername;
    }

    @Override
    public EnumNotifications getType() {
        return EnumNotifications.REQUEST;
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
        return this.index;
    }

    @Override
    public int getExpireTime() {
        return ExchangeConfig.EXCHANGE_REQUEST_EXPIRE_TIME.getIntValue();
    }

    @Override
    public void accepted(EntityPlayer player) {        
        if (OxygenHelperServer.isOnline(this.senderUUID)) {
            ExchangeManagerServer.instance().processExchangeRequestReply(player, this.senderUUID);
            OxygenHelperServer.sendMessage(CommonReference.playerByUUID(this.senderUUID), ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeChatMessages.EXCHANGE_REQUEST_ACCEPTED_SENDER.ordinal());
        }

        OxygenHelperServer.sendMessage(player, ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeChatMessages.EXCHANGE_REQUEST_ACCEPTED_TARGET.ordinal());

        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }

    @Override
    public void rejected(EntityPlayer player) {
        if (OxygenHelperServer.isOnline(this.senderUUID))
            OxygenHelperServer.sendMessage(CommonReference.playerByUUID(this.senderUUID), ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeChatMessages.EXCHANGE_REQUEST_REJECTED_SENDER.ordinal());
        OxygenHelperServer.sendMessage(player, ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeChatMessages.EXCHANGE_REQUEST_REJECTED_TARGET.ordinal());

        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }

    @Override
    public void expired() {
        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }
}
