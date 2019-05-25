package austeretony.oxygen_exchange.client;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen_exchange.client.gui.exchange.ExchangeMenuGUIContainer;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import austeretony.oxygen_exchange.common.main.ExchangeProcess.EnumExchangeOperation;
import austeretony.oxygen_exchange.common.network.server.SPExchangeMenuOperation;
import austeretony.oxygen_exchange.common.network.server.SPSendExchangeRequest;
import net.minecraft.item.ItemStack;

public class ExchangeManagerClient {

    private static ExchangeManagerClient instance;

    private String username = "Undefined";//latest player username you sent exchange request, used in GUI

    public static void create() {
        if (instance == null)
            instance = new ExchangeManagerClient();
    }

    public static ExchangeManagerClient instance() {
        return instance;
    }

    public void sendExchangeRequestSynced(UUID playerUUID) {
        SharedPlayerData sharedData = OxygenHelperClient.getSharedPlayerData(playerUUID);
        this.username = sharedData.getUsername();
        ExchangeMain.network().sendToServer(new SPSendExchangeRequest(sharedData.getIndex()));
    }

    public String getRequestedUsername() {
        return this.username;
    }

    public void offerExchangeSynced(int offeredCurrency) {
        ExchangeMain.network().sendToServer(new SPExchangeMenuOperation(offeredCurrency));
    }

    public void cancelExchangeSynced() {
        ExchangeMain.network().sendToServer(new SPExchangeMenuOperation(EnumExchangeOperation.CANCEL));
    }

    public void confirmExchangeSynced() {
        ExchangeMain.network().sendToServer(new SPExchangeMenuOperation(EnumExchangeOperation.CONFIRM));
    }

    public void closeExchangeMenuSynced() {
        ExchangeMain.network().sendToServer(new SPExchangeMenuOperation(EnumExchangeOperation.CLOSE));
    }

    public void setOtherPlayerOffer(int offeredCurrency, ItemStack[] offeredItems) {
        if (ClientReference.hasActiveGUI() 
                && ClientReference.getMinecraft().currentScreen instanceof ExchangeMenuGUIContainer) {
            ExchangeMenuGUIContainer guiContainer = (ExchangeMenuGUIContainer) ClientReference.getMinecraft().currentScreen;
            for (int i = 0; i < 5; i++)
                guiContainer.container.inventorySlots.get(i).putStack(offeredItems[i]);
            guiContainer.getMainSection().setOtherOfferedCurrency(offeredCurrency);
        }
    }

    public void resetOtherPlayerOffer() {       
        if (ClientReference.hasActiveGUI() 
                && ClientReference.getMinecraft().currentScreen instanceof ExchangeMenuGUIContainer) {
            ExchangeMenuGUIContainer guiContainer = (ExchangeMenuGUIContainer) ClientReference.getMinecraft().currentScreen;
            for (int i = 0; i < 5; i++)
                guiContainer.container.inventorySlots.get(i).putStack(ItemStack.EMPTY);
            guiContainer.getMainSection().resetOtherOffer();   
            
            guiContainer.container.enableClientOfferSlots();
        }
    }

    public void setOtherPlayerConfirmedExchange() {
        if (ClientReference.hasActiveGUI() 
                && ClientReference.getMinecraft().currentScreen instanceof ExchangeMenuGUIContainer) {
            ExchangeMenuGUIContainer guiContainer = (ExchangeMenuGUIContainer) ClientReference.getMinecraft().currentScreen;
            guiContainer.getMainSection().setOtherConfirmedExchange();
        }
    }
}
