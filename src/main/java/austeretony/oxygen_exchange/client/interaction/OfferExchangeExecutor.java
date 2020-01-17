package austeretony.oxygen_exchange.client.interaction;

import java.util.UUID;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.interaction.PlayerInteractionMenuEntry;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.common.main.EnumExchangePrivilege;

public class OfferExchangeExecutor implements PlayerInteractionMenuEntry {

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_exchange.gui.interaction.offerExchange");
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return OxygenHelperClient.isPlayerAvailable(playerUUID) && PrivilegesProviderClient.getAsBoolean(EnumExchangePrivilege.ALLOW_EXCHANGE.id(), true);
    }

    @Override
    public void execute(UUID playerUUID) {
        ExchangeManagerClient.instance().getExchangeOperationsManager().sendExchangeRequestSynced(playerUUID);
    }
}
