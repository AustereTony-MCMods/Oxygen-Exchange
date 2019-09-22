package austeretony.oxygen_exchange.client.gui.exchange;

import austeretony.alternateui.container.framework.GUISlotsFramework;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.alternateui.util.EnumGUISlotsPosition;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.WatcherHelperClient;
import austeretony.oxygen_core.client.gui.elements.CurrencyValueGUIElement;
import austeretony.oxygen_core.client.gui.elements.InventoryLoadGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.server.OxygenPlayerData;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.gui.exchange.callback.ConfirmationGUICallback;
import net.minecraft.item.ItemStack;

public class ExchangeGUISection extends AbstractGUISection {

    private final ExchangeMenuGUIContainer screen;

    private GUISlotsFramework inventorySlots, hotbarSlots, clientOfferSlots, otherOfferSlots;            

    private OxygenGUITextField currencyField;

    private OxygenGUIText clientConfirmedTextLabel, otherConfirmedTextLabel;

    private OxygenGUIButton offerButton, cancelButton, confirmButton;

    private CurrencyValueGUIElement balanceElement, otherPlayerOfferedCurrencyElement;

    private InventoryLoadGUIElement inventoryLoadElement;

    private AbstractGUICallback confirmExchangeCallback;

    public final String 
    exchangeConfirmed = ClientReference.localize("oxygen_exchange.gui.exchange.confirmed"),
    exchangeNotConfirmed = ClientReference.localize("oxygen_exchange.gui.exchange.notConfirmed");

    public ExchangeGUISection(ExchangeMenuGUIContainer screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new ExchangeMenuGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_exchange.gui.exchange.title"), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(new OxygenGUIText(6, 18, ClientReference.localize("oxygen_exchange.gui.exchange.playerOffer", ExchangeManagerClient.instance().getExchangeOperationsManager().getRequestedUsername()), 
                GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(new OxygenGUIText(6, 54, ClientReference.localize("oxygen_exchange.gui.exchange.yourOffer"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));

        long balance = WatcherHelperClient.getLong(OxygenPlayerData.CURRENCY_COINS_WATCHER_ID);
        this.addElement(this.currencyField = new OxygenGUITextField(100, 68, 40, 8, 10, "0", 3, true, balance));

        this.addElement(this.otherPlayerOfferedCurrencyElement = new CurrencyValueGUIElement(105, 35));   
        this.otherPlayerOfferedCurrencyElement.setValue(0L);

        this.addElement(this.inventoryLoadElement = new InventoryLoadGUIElement(4, this.getHeight() - 9, EnumGUIAlignment.RIGHT));
        this.inventoryLoadElement.updateLoad();
        this.addElement(this.balanceElement = new CurrencyValueGUIElement(this.getWidth() - 10, this.getHeight() - 10));   
        this.balanceElement.setValue(balance);

        this.addElement(this.otherConfirmedTextLabel = new OxygenGUIText(6, 44, this.exchangeNotConfirmed, GUISettings.get().getSubTextScale() - 0.05F, GUISettings.get().getEnabledTextColorDark()));
        this.addElement(this.clientConfirmedTextLabel = new OxygenGUIText(6, 80, this.exchangeNotConfirmed, GUISettings.get().getSubTextScale() - 0.05F, GUISettings.get().getEnabledTextColorDark()));

        this.addElement(this.offerButton = new OxygenGUIButton(6, 88, 40, 10, ClientReference.localize("oxygen_exchange.gui.exchange.offerButton")));  
        this.addElement(this.cancelButton = new OxygenGUIButton(50, 88, 40, 10, ClientReference.localize("oxygen.gui.cancelButton")).disable());  
        this.addElement(this.confirmButton = new OxygenGUIButton(100, 88, 40, 10, ClientReference.localize("oxygen.gui.confirmButton")).disable());   

        this.addSlotsFramework(this.clientOfferSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 5, 10, 1, 5).setPosition(6, 61)
                .enableSlotBottomLayer().setSlotBottomLayerColor(0xFF050505).setSlotHighlightingColor(0x45FFFFFF));
        this.addSlotsFramework(this.otherOfferSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 0, 5, 1, 5).setPosition(6, 25)
                .enableSlotBottomLayer().setSlotBottomLayerColor(0xFF050505).setSlotHighlightingColor(0x45FFFFFF));
        this.addSlotsFramework(this.inventorySlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 10, 37, 3, 9).setPosition(6, 102)
                .enableSlotBottomLayer().setSlotBottomLayerColor(0xFF050505).setSlotHighlightingColor(0x45FFFFFF));
        this.addSlotsFramework(this.hotbarSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 37, 46, 1, 9).setPosition(6, 158)
                .enableSlotBottomLayer().setSlotBottomLayerColor(0xFF050505).setSlotHighlightingColor(0x45FFFFFF));
        this.clientOfferSlots.updateFramework();
        this.otherOfferSlots.updateFramework(); 
        this.inventorySlots.updateFramework();
        this.hotbarSlots.updateFramework();
        this.otherOfferSlots.disable();

        this.confirmExchangeCallback = new ConfirmationGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.offerButton)
                ExchangeManagerClient.instance().getExchangeOperationsManager().makeOfferSynced(this.currencyField.getTypedNumber());
            else if (element == this.cancelButton)
                ExchangeManagerClient.instance().getExchangeOperationsManager().cancelExchangeSynced();
            else if (element == this.confirmButton)
                this.confirmExchangeCallback.open();
        }
    }

    public void madeOffer() {
        this.cancelButton.enable();
        this.confirmButton.enable();
        this.offerButton.disable();
        this.currencyField.disable();
    }

    public void canceledExchange() {
        this.cancelButton.disable();
        this.confirmButton.disable();
        this.offerButton.enable();
        this.currencyField.enable();
        this.otherPlayerOfferedCurrencyElement.setValue(0L);
        this.clientConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);  
    }

    public void confirmedExchange() {
        this.confirmButton.disable();
        this.clientConfirmedTextLabel.setDisplayText(this.exchangeConfirmed);
    }

    public void otherPlayerMadeOffer(long offeredCurrency, ItemStack[] offeredItems) {
        this.otherPlayerOfferedCurrencyElement.setValue(offeredCurrency);
        this.otherPlayerOfferedCurrencyElement.setX(102 + this.textWidth(this.otherPlayerOfferedCurrencyElement.getDisplayText(), this.otherPlayerOfferedCurrencyElement.getTextScale()));
    }

    public void otherPlayerCanceledExchange() { 
        this.cancelButton.disable();
        this.confirmButton.disable();
        this.offerButton.enable();
        this.currencyField.enable();
        this.otherPlayerOfferedCurrencyElement.setValue(0L);
        this.clientConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);  
    }

    public void otherPlayerConfirmedExchange() {
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeConfirmed);
    }
}
