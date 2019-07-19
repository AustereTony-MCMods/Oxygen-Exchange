package austeretony.oxygen_exchange.client.event;

import austeretony.oxygen.client.api.event.OxygenChatMessageEvent;
import austeretony.oxygen.client.api.event.OxygenNotificationRecievedEvent;
import austeretony.oxygen.common.notification.EnumNotification;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.common.main.EnumExchangeChatMessages;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExchangeEventsClient {

    @SubscribeEvent
    public void onChatMessage(OxygenChatMessageEvent event) {
        if (event.modIndex == ExchangeMain.EXCHANGE_MOD_INDEX)
            EnumExchangeChatMessages.values()[event.messageIndex].show(event.args);
    }

    @SubscribeEvent
    public void onNotificationRecieve(OxygenNotificationRecievedEvent event) {
        if (event.notification.getType() == EnumNotification.REQUEST 
                && event.notification.getIndex() == ExchangeMain.EXCHANGE_REQUEST_ID)
            ExchangeManagerClient.instance().setRequestedUsername(event.notification.getArguments()[0]);
    }
}
