package austeretony.oxygen_exchange.client.event;

import austeretony.oxygen_core.client.api.event.OxygenNotificationRecievedEvent;
import austeretony.oxygen_core.common.notification.EnumNotification;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExchangeEventsClient {

    @SubscribeEvent
    public void onNotificationRecieve(OxygenNotificationRecievedEvent event) {
        if (event.notification.getType() == EnumNotification.REQUEST 
                && event.notification.getIndex() == ExchangeMain.EXCHANGE_REQUEST_ID)
            ExchangeManagerClient.instance().getExchangeOperationsManager().setRequestedUsername(event.notification.getArguments()[0]);
    }
}
