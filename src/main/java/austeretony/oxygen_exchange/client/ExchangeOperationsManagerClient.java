package austeretony.oxygen_exchange.client;

import java.util.UUID;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_exchange.common.EnumExchangeOperation;
import austeretony.oxygen_exchange.common.network.server.SPExchangeMenuOperation;
import austeretony.oxygen_exchange.common.network.server.SPSendExchangeRequest;

public class ExchangeOperationsManagerClient {

    private String username = "Undefined";

    public void sendExchangeRequestSynced(UUID playerUUID) {
        PlayerSharedData sharedData = OxygenHelperClient.getPlayerSharedData(playerUUID);
        this.username = sharedData.getUsername();
        OxygenMain.network().sendToServer(new SPSendExchangeRequest(sharedData.getIndex()));
    }

    public String getRequestedUsername() {
        return this.username;
    }

    public void setRequestedUsername(String username) {
        this.username = username;
    }

    public void makeOfferSynced(long offeredCurrency) {
        OxygenMain.network().sendToServer(new SPExchangeMenuOperation(EnumExchangeOperation.OFFER, offeredCurrency));
    }

    public void cancelExchangeSynced() {
        OxygenMain.network().sendToServer(new SPExchangeMenuOperation(EnumExchangeOperation.CANCEL, 0L));
    }

    public void confirmExchangeSynced() {
        OxygenMain.network().sendToServer(new SPExchangeMenuOperation(EnumExchangeOperation.CONFIRM, 0L));
    }

    public void closeExchangeMenuSynced() {
        OxygenMain.network().sendToServer(new SPExchangeMenuOperation(EnumExchangeOperation.CLOSE, 0L));
    }
}
