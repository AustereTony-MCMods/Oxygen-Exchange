package austeretony.oxygen_exchange.server.event;

import austeretony.oxygen_core.server.api.event.OxygenPlayerUnloadedEvent;
import austeretony.oxygen_exchange.server.ExchangeManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExchangeEventsServer {

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerUnloadedEvent event) {
        ExchangeManagerServer.instance().onPlayerUnloaded(event.playerMP);
    }
}
