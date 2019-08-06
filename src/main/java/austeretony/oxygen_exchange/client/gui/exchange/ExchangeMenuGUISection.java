package austeretony.oxygen_exchange.client.gui.exchange;

import austeretony.alternateui.container.framework.GUISlotsFramework;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUISlotsPosition;
import austeretony.oxygen.client.api.WatcherHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.BalanceGUIElement;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.gui.exchange.callback.ConfirmationGUICallback;
import net.minecraft.item.ItemStack;

public class ExchangeMenuGUISection extends AbstractGUISection {

    private final ExchangeMenuGUIContainer screen;

    private GUISlotsFramework inventorySlots, hotbarSlots, clientOfferSlots, otherOfferSlots;            

    private GUITextField currencyField;

    private GUIImageLabel coinIconFirst, coinIconSecond;

    private GUITextLabel otherCurrencyOfferTextLabel, clientConfirmedTextLabel, otherConfirmedTextLabel;

    private GUIButton offerButton, cancelButton, confirmButton;

    private BalanceGUIElement balanceElement;

    private AbstractGUICallback confirmationCallback;

    public final String 
    exchangeConfirmed = ClientReference.localize("oxygen_exchange.gui.exchange.confirmed"),
    exchangeNotConfirmed = ClientReference.localize("oxygen_exchange.gui.exchange.notConfirmed");

    public ExchangeMenuGUISection(ExchangeMenuGUIContainer screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new ExchangeMenuGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 4).setDisplayText(ClientReference.localize("oxygen_exchange.gui.exchange.title"), false, GUISettings.instance().getTitleScale()));

        this.addElement(new GUITextLabel(2, 15).setDisplayText(ClientReference.localize("oxygen_exchange.gui.exchange.yourOffer"), false, GUISettings.instance().getTextScale()));
        this.addElement(new GUITextLabel(99, 15).setDisplayText(ClientReference.localize("oxygen_exchange.gui.exchange.playerOffer", ExchangeManagerClient.instance().getRequestedUsername()), false, GUISettings.instance().getTextScale()));

        this.addElement(this.currencyField = new GUITextField(10, 44, 45, 9, 10).setTextScale(GUISettings.instance().getSubTextScale())
                .enableDynamicBackground(GUISettings.instance().getEnabledTextFieldColor(), GUISettings.instance().getDisabledTextFieldColor(), GUISettings.instance().getHoveredTextFieldColor())
                .setText("0").setLineOffset(3).enableNumberFieldMode(WatcherHelperClient.getInt(OxygenPlayerData.CURRENCY_COINS_WATCHER_ID)).cancelDraggedElementLogic());
        this.addElement(this.coinIconFirst = new GUIImageLabel(2, 46, 6, 6).setTexture(OxygenGUITextures.COIN_ICON, 6, 6));

        this.addElement(this.otherCurrencyOfferTextLabel = new GUITextLabel(107, 45).setDisplayText("0", false, GUISettings.instance().getSubTextScale()));
        this.addElement(this.coinIconSecond = new GUIImageLabel(99, 46, 6, 6).setTexture(OxygenGUITextures.COIN_ICON, 6, 6));

        this.addElement(this.balanceElement = new BalanceGUIElement(this.getWidth() - 8, 68).setEnabledTextColor(GUISettings.instance().getEnabledTextColor())
                .setBalance(WatcherHelperClient.getInt(OxygenPlayerData.CURRENCY_COINS_WATCHER_ID))); 

        this.addElement(this.clientConfirmedTextLabel = new GUITextLabel(2, 54).setDisplayText(this.exchangeNotConfirmed, false, GUISettings.instance().getSubTextScale()));
        this.addElement(this.otherConfirmedTextLabel = new GUITextLabel(99, 54).setDisplayText(this.exchangeNotConfirmed, false, GUISettings.instance().getSubTextScale()));

        this.addElement(this.offerButton = new GUIButton(4, 66, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen_exchange.gui.exchange.offerButton"), true, GUISettings.instance().getButtonTextScale()));  

        this.addElement(this.cancelButton = new GUIButton(48, 66, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));  

        this.addElement(this.confirmButton = new GUIButton(99, 66, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()));   

        this.addSlotsFramework(this.clientOfferSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 5, 10, 1, 5).setPosition(4, 26).enableSlotBottomLayer());
        this.addSlotsFramework(this.otherOfferSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 0, 5, 1, 5).setPosition(101, 26).enableSlotBottomLayer());
        this.addSlotsFramework(this.inventorySlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 10, 37, 3, 9).setPosition(16, 80).enableSlotBottomLayer());
        this.addSlotsFramework(this.hotbarSlots = new GUISlotsFramework(EnumGUISlotsPosition.CUSTOM, this.screen.container, 37, 46, 1, 9).setPosition(16, 136).enableSlotBottomLayer());
        this.clientOfferSlots.updateFramework();
        this.otherOfferSlots.updateFramework(); 
        this.inventorySlots.updateFramework();
        this.hotbarSlots.updateFramework();
        this.otherOfferSlots.disable();

        this.confirmationCallback = new ConfirmationGUICallback(this.screen, this, 140, 40).enableDefaultBackground();

        this.cancelButton.disable();
        this.confirmButton.disable();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.offerButton) {
                this.offerButton.disable();
                this.cancelButton.enable();
                this.confirmButton.enable();
                int currency = 0;
                this.currencyField.disable();
                currency = this.currencyField.getTypedNumber();
                ExchangeManagerClient.instance().offerExchangeSynced(currency);
                this.screen.container.disableClientOfferSlots();
            } else if (element == this.cancelButton) {
                this.cancelButton.disable();
                this.confirmButton.disable();
                this.offerButton.enable();
                ExchangeManagerClient.instance().cancelExchangeSynced();
                this.currencyField.enable();
                this.otherCurrencyOfferTextLabel.setDisplayText("0");
                this.screen.container.enableClientOfferSlots();     
                for (int i = 0; i < 5; i++)
                    this.screen.container.inventorySlots.get(i).putStack(ItemStack.EMPTY);
            } else if (element == this.confirmButton) {
                this.confirmationCallback.open();
                this.confirmButton.disable();
                this.clientConfirmedTextLabel.setDisplayText(this.exchangeConfirmed);
            }
        }
    }

    public void updateCurrencyAmount() {
        this.balanceElement.setBalance(WatcherHelperClient.getInt(OxygenPlayerData.CURRENCY_COINS_WATCHER_ID));
    }

    public void enableConfirmButton() {
        this.confirmButton.enable();
    }

    public void setOtherOfferedCurrency(int value) {
        this.otherCurrencyOfferTextLabel.setDisplayText(String.valueOf(value));
    }

    public void setOtherConfirmedExchange() {
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeConfirmed);
    }

    public void resetOtherOffer() {
        this.currencyField.enable();
        this.otherCurrencyOfferTextLabel.setDisplayText("0");
        this.offerButton.enable();
        this.cancelButton.disable();
        this.confirmButton.disable();
        this.clientConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);
        this.otherConfirmedTextLabel.setDisplayText(this.exchangeNotConfirmed);  
    }
}
