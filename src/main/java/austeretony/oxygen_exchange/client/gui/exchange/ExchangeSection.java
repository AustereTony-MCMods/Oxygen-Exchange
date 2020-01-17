package austeretony.oxygen_exchange.client.gui.exchange;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.container.framework.GUISlotsFramework;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.util.EnumGUISlotsPosition;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.WatcherHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenCurrencyValue;
import austeretony.oxygen_core.client.gui.elements.OxygenInventoryLoad;
import austeretony.oxygen_core.client.gui.elements.OxygenNumberField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.gui.exchange.callback.ConfirmExchangeCallback;
import austeretony.oxygen_exchange.client.settings.gui.EnumExchangeGUISetting;
import net.minecraft.item.ItemStack;

public class ExchangeSection extends AbstractGUISection {

    private final ExchangeMenuContainer screen;

    private GUISlotsFramework inventorySlots, hotbarSlots, clientOfferSlots, otherOfferSlots;            

    private OxygenNumberField currencyField;

    private OxygenTextLabel clientConfirmedTextLabel, otherConfirmedTextLabel;

    private OxygenButton offerButton, cancelButton, confirmButton;

    private OxygenCurrencyValue balanceValue, otherPlayerOfferedCurrencyValue;

    private OxygenInventoryLoad inventoryLoad;

    private AbstractGUICallback confirmExchangeCallback;

    public final String 
    exchangeConfirmed = ClientReference.localize("oxygen_exchange.gui.exchange.confirmed"),
    exchangeNotConfirmed = ClientReference.localize("oxygen_exchange.gui.exchange.notConfirmed");

    public ExchangeSection(ExchangeMenuContainer screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new ExchangeMenuBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_exchange.gui.exchange.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(new OxygenTextLabel(6, 23, ClientReference.localize("oxygen_exchange.gui.exchange.playerOffer", ExchangeManagerClient.instance().getExchangeOperationsManager().getRequestedUsername()), 
                EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(new OxygenTextLabel(6, 67, ClientReference.localize("oxygen_exchange.gui.exchange.yourOffer"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        long balance = WatcherHelperClient.getLong(OxygenMain.COMMON_CURRENCY_INDEX);
        this.addElement(this.currencyField = new OxygenNumberField(6, 96, 40, "0", balance, false, 0, true));

        this.addElement(this.otherPlayerOfferedCurrencyValue = new OxygenCurrencyValue(11, 50));   
        this.otherPlayerOfferedCurrencyValue.setValue(OxygenMain.COMMON_CURRENCY_INDEX, 0L);

        this.addElement(this.inventoryLoad = new OxygenInventoryLoad(6, this.getHeight() - 8));
        this.inventoryLoad.updateLoad();
        this.addElement(this.balanceValue = new OxygenCurrencyValue(this.getWidth() - 14, this.getHeight() - 10));   
        this.balanceValue.setValue(OxygenMain.COMMON_CURRENCY_INDEX, balance);

        this.addElement(this.otherConfirmedTextLabel = new OxygenTextLabel(6, 30, this.exchangeNotConfirmed, EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.INACTIVE_TEXT_COLOR.get().asInt()));
        this.addElement(this.clientConfirmedTextLabel = new OxygenTextLabel(6, 74, this.exchangeNotConfirmed, EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.INACTIVE_TEXT_COLOR.get().asInt()));

        this.addElement(this.offerButton = new OxygenButton(6, 108, 40, 10, ClientReference.localize("oxygen_core.gui.offer")));
        this.offerButton.setKeyPressListener(Keyboard.KEY_F, ()->this.makeOffer());

        this.addElement(this.cancelButton = new OxygenButton(50, 108, 40, 10, ClientReference.localize("oxygen_core.gui.cancel")).disable());  
        this.cancelButton.setKeyPressListener(Keyboard.KEY_X, ()->this.cancelOffer());

        this.addElement(this.confirmButton = new OxygenButton(100, 108, 40, 10, ClientReference.localize("oxygen_core.gui.confirm")).disable());   
        this.confirmButton.setKeyPressListener(Keyboard.KEY_R, ()->this.confirmOffer());

        int 
        slotBottomColor = EnumExchangeGUISetting.SLOT_BOTTOM_LAYER_COLOR.get().asInt(),
        slotHighlightingColor = EnumExchangeGUISetting.SLOT_HIGHLIGHTING_COLOR.get().asInt();
        this.addSlotsFramework(this.clientOfferSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 5, 10, 1, 5).setPosition(6, 76)
                .enableSlotBottomLayer().setSlotBottomLayerColor(slotBottomColor).setSlotHighlightingColor(slotHighlightingColor));
        this.addSlotsFramework(this.otherOfferSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 0, 5, 1, 5).setPosition(6, 32)
                .enableSlotBottomLayer().setSlotBottomLayerColor(slotBottomColor).setSlotHighlightingColor(slotHighlightingColor));
        this.addSlotsFramework(this.inventorySlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 10, 37, 3, 9).setPosition(6, 122)
                .enableSlotBottomLayer().setSlotBottomLayerColor(slotBottomColor).setSlotHighlightingColor(slotHighlightingColor));
        this.addSlotsFramework(this.hotbarSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 37, 46, 1, 9).setPosition(6, 180)
                .enableSlotBottomLayer().setSlotBottomLayerColor(slotBottomColor).setSlotHighlightingColor(slotHighlightingColor));
        this.clientOfferSlots.updateFramework();
        this.otherOfferSlots.updateFramework(); 
        this.inventorySlots.updateFramework();
        this.hotbarSlots.updateFramework();
        this.otherOfferSlots.disable();

        this.confirmExchangeCallback = new ConfirmExchangeCallback(this.screen, this, 140, 40).enableDefaultBackground();
    }

    private void makeOffer() {
        ExchangeManagerClient.instance().getExchangeOperationsManager().makeOfferSynced(this.currencyField.getTypedNumberAsLong());
    }

    private void cancelOffer() {
        ExchangeManagerClient.instance().getExchangeOperationsManager().cancelExchangeSynced();
    }

    private void confirmOffer() {
        this.confirmExchangeCallback.open();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.offerButton)
                this.makeOffer();
            else if (element == this.cancelButton)
                this.cancelOffer();
            else if (element == this.confirmButton)
                this.confirmOffer();
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
        this.otherPlayerOfferedCurrencyValue.updateValue(0L);
        this.otherPlayerOfferedCurrencyValue.setX(11);
        this.clientConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);  
        this.clientConfirmedTextLabel.setEnabledTextColor(EnumBaseGUISetting.INACTIVE_TEXT_COLOR.get().asInt());
        this.otherConfirmedTextLabel.setEnabledTextColor(EnumBaseGUISetting.INACTIVE_TEXT_COLOR.get().asInt());
    }

    public void confirmedExchange() {
        this.confirmButton.disable();
        this.clientConfirmedTextLabel.setDisplayText(this.exchangeConfirmed);
        this.clientConfirmedTextLabel.setEnabledTextColor(EnumBaseGUISetting.ACTIVE_TEXT_COLOR.get().asInt());
    }

    public void otherPlayerMadeOffer(long offeredCurrency, ItemStack[] offeredItems) {
        this.otherPlayerOfferedCurrencyValue.updateValue(offeredCurrency);
        this.otherPlayerOfferedCurrencyValue.setX(8 + this.textWidth(this.otherPlayerOfferedCurrencyValue.getDisplayText(), this.otherPlayerOfferedCurrencyValue.getTextScale()));
    }

    public void otherPlayerCanceledExchange() { 
        this.cancelButton.disable();
        this.confirmButton.disable();
        this.offerButton.enable();
        this.currencyField.enable();
        this.otherPlayerOfferedCurrencyValue.updateValue(0L);
        this.otherPlayerOfferedCurrencyValue.setX(11);
        this.clientConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);  
        this.clientConfirmedTextLabel.setEnabledTextColor(EnumBaseGUISetting.INACTIVE_TEXT_COLOR.get().asInt());
        this.otherConfirmedTextLabel.setEnabledTextColor(EnumBaseGUISetting.INACTIVE_TEXT_COLOR.get().asInt());
    }

    public void otherPlayerConfirmedExchange() {
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeConfirmed);
        this.otherConfirmedTextLabel.setEnabledTextColor(EnumBaseGUISetting.ACTIVE_TEXT_COLOR.get().asInt());
    }
}
