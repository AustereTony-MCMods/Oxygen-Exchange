package austeretony.oxygen_exchange.client.gui.exchange;

import java.util.UUID;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.interaction.InteractionMenuEntry;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;

public class OfferExchangeExecutor implements InteractionMenuEntry {

    @Override
    public String getName() {
        return "oxygen_exchange.gui.interaction.offerExchange";
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return !OxygenHelperClient.isOfflineStatus(playerUUID);
    }

    @Override
    public void execute(UUID playerUUID) {
        ExchangeManagerClient.instance().getExchangeOperationsManager().sendExchangeRequestSynced(playerUUID);
    }
}
