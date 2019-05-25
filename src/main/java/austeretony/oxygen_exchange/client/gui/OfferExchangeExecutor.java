package austeretony.oxygen_exchange.client.gui;

import java.util.UUID;

import austeretony.oxygen.client.IInteractionExecutor;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import net.minecraft.util.ResourceLocation;

public class OfferExchangeExecutor implements IInteractionExecutor {

    @Override
    public String getName() {
        return "oxygen_exchange.gui.interaction.offerExchange";
    }

    @Override
    public ResourceLocation getIcon() {
        return ExchangeGUITextures.EXCHANGE_ICONS;
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return !OxygenHelperClient.isOfflineStatus(playerUUID);
    }

    @Override
    public void execute(UUID playerUUID) {
        ExchangeManagerClient.instance().sendExchangeRequestSynced(playerUUID);
    }
}
