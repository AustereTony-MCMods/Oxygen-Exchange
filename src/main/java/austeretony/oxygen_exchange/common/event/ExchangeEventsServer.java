package austeretony.oxygen_exchange.common.event;

import austeretony.oxygen.common.api.event.OxygenPlayerUnloadedEvent;
import austeretony.oxygen_exchange.common.ExchangeManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExchangeEventsServer {

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerUnloadedEvent event) {
        ExchangeManagerServer.instance().onPlayerUnloaded(event.player);
    }
}