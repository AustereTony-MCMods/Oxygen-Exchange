package austeretony.oxygen_exchange.client.gui.exchange;

import austeretony.alternateui.container.framework.GUISlotsFramework;
import austeretony.alternateui.container.framework.GUISlotsFramework.GUIEnumPosition;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.api.StatWatcherHelperClient;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.config.OxygenConfig;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.gui.exchange.callback.ConfirmExchangeGUICallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class ExchangeMenuGUISection extends AbstractGUISection {

    private final ExchangeMenuGUIContainer screen;

    private GUISlotsFramework inventorySlots, hotbarSlots, clientOfferSlots, otherOfferSlots;            

    private GUITextField currencyField;

    private GUIImageLabel coinIconFirst, coinIconSecond, coinIconThird;

    private GUITextLabel clientCurrencyAmountTextLabel, otherCurrencyOfferTextLabel, clientConfirmedTextLabel, otherConfirmedTextLabel;

    private GUIButton offerButton, cancelButton, confirmButton;

    private AbstractGUICallback confirmCallback;

    public static final String 
    EXCHANGE_CONFIRMED = I18n.format("oxygen_exchange.gui.exchange.confirmed"),
    EXCHANGE_NOT_CONFIRMED = I18n.format("oxygen_exchange.gui.exchange.notConfirmed");

    public ExchangeMenuGUISection(ExchangeMenuGUIContainer screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new ExchangeMenuBackgroundGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 4).setDisplayText(I18n.format("oxygen_exchange.gui.exchange.title"), false, GUISettings.instance().getTitleScale()));

        this.addElement(new GUITextLabel(2, 15).setDisplayText(I18n.format("oxygen_exchange.gui.exchange.yourOffer"), false, GUISettings.instance().getTextScale()));
        this.addElement(new GUITextLabel(99, 15).setDisplayText(I18n.format("oxygen_exchange.gui.exchange.playerOffer", ExchangeManagerClient.instance().getRequestedUsername()), false, GUISettings.instance().getTextScale()));

        if (OxygenConfig.ENABLE_CURRENCY.getBooleanValue()) {
            this.addElement(this.currencyField = new GUITextField(10, 46, 40, 10).setScale(0.7F).setText("0").enableNumberFieldMode(StatWatcherHelperClient.getInt(OxygenMain.CURRENCY_GOLD_STAT_ID)).cancelDraggedElementLogic());
            this.addElement(this.coinIconFirst = new GUIImageLabel(2, 46, 6, 6).setTexture(OxygenGUITextures.GOLD_COIN_ICON, 6, 6));

            this.addElement(this.otherCurrencyOfferTextLabel = new GUITextLabel(107, 46).setDisplayText("0", false, GUISettings.instance().getTextScale()));
            this.addElement(this.coinIconSecond = new GUIImageLabel(99, 46, 6, 6).setTexture(OxygenGUITextures.GOLD_COIN_ICON, 6, 6));

            this.addElement(this.clientCurrencyAmountTextLabel = new GUITextLabel(0, 67).setTextScale(GUISettings.instance().getSubTextScale()));
            this.addElement(this.coinIconThird = new GUIImageLabel(0, 68, 6, 6).setTexture(OxygenGUITextures.GOLD_COIN_ICON, 6, 6).initSimpleTooltip(I18n.format("oxygen.currency.gold"), GUISettings.instance().getTooltipScale()));
            this.updateCurrencyAmount();
        }

        this.addElement(this.clientConfirmedTextLabel = new GUITextLabel(2, 54).setDisplayText(EXCHANGE_NOT_CONFIRMED, false, GUISettings.instance().getSubTextScale()));
        this.addElement(this.otherConfirmedTextLabel = new GUITextLabel(99, 54).setDisplayText(EXCHANGE_NOT_CONFIRMED, false, GUISettings.instance().getSubTextScale()));

        this.addElement(this.offerButton = new GUIButton(4, 66, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(I18n.format("oxygen_exchange.gui.exchange.offerButton"), true, GUISettings.instance().getButtonTextScale()));  

        this.addElement(this.cancelButton = new GUIButton(48, 66, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(I18n.format("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));  

        this.addElement(this.confirmButton = new GUIButton(99, 66, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(I18n.format("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()));   

        this.addSlotsFramework(this.clientOfferSlots = new GUISlotsFramework(GUIEnumPosition.CUSTOM, this.screen.container, 5, 10, 1, 5).setPosition(4, 26).enableSlotBottomLayer());
        this.addSlotsFramework(this.otherOfferSlots = new GUISlotsFramework(GUIEnumPosition.CUSTOM, this.screen.container, 0, 5, 1, 5).setPosition(101, 26).enableSlotBottomLayer());
        this.addSlotsFramework(this.inventorySlots = new GUISlotsFramework(GUIEnumPosition.CUSTOM, this.screen.container, 10, 37, 3, 9).setPosition(16, 80).enableSlotBottomLayer());
        this.addSlotsFramework(this.hotbarSlots = new GUISlotsFramework(GUIEnumPosition.CUSTOM, this.screen.container, 37, 46, 1, 9).setPosition(16, 136).enableSlotBottomLayer());
        this.clientOfferSlots.updateFramework();
        this.otherOfferSlots.updateFramework(); 
        this.inventorySlots.updateFramework();
        this.hotbarSlots.updateFramework();
        this.otherOfferSlots.disable();

        this.confirmCallback = new ConfirmExchangeGUICallback(this.screen, this, 140, 40).enableDefaultBackground();

        this.cancelButton.disable();
        this.confirmButton.disable();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (element == this.offerButton) {
            this.offerButton.disable();
            this.cancelButton.enable();
            this.confirmButton.enable();
            int currency = 0;
            if (OxygenConfig.ENABLE_CURRENCY.getBooleanValue()) {
                this.currencyField.disable();
                currency = this.currencyField.getTypedNumber();
            }
            ExchangeManagerClient.instance().offerExchangeSynced(currency);
            this.screen.container.disableClientOfferSlots();
        } else if (element == this.cancelButton) {
            this.cancelButton.disable();
            this.confirmButton.disable();
            this.offerButton.enable();
            ExchangeManagerClient.instance().cancelExchangeSynced();
            if (OxygenConfig.ENABLE_CURRENCY.getBooleanValue()) {
                this.currencyField.enable();
                this.otherCurrencyOfferTextLabel.setDisplayText("0");
            }
            this.screen.container.enableClientOfferSlots();     
            for (int i = 0; i < 5; i++)
                this.screen.container.inventorySlots.get(i).putStack(ItemStack.EMPTY);
        } else if (element == this.confirmButton) {
            this.confirmCallback.open();
            this.confirmButton.disable();
            this.clientConfirmedTextLabel.setDisplayText(EXCHANGE_CONFIRMED);
        }
    }

    public void updateCurrencyAmount() {
        String currency = String.valueOf(StatWatcherHelperClient.getInt(OxygenMain.CURRENCY_GOLD_STAT_ID));
        this.coinIconThird.setX(this.getWidth() - this.textWidth(currency, GUISettings.instance().getSubTextScale()) - 10);
        this.clientCurrencyAmountTextLabel.setX(this.getWidth() - this.textWidth(currency, GUISettings.instance().getSubTextScale()) - 2);
        this.clientCurrencyAmountTextLabel.setDisplayText(currency);
    }

    public void enableConfirmButton() {
        this.confirmButton.enable();
    }

    public void setOtherOfferedCurrency(int value) {
        if (OxygenConfig.ENABLE_CURRENCY.getBooleanValue())
            this.otherCurrencyOfferTextLabel.setDisplayText(String.valueOf(value));
    }

    public void setOtherConfirmedExchange() {
        this.otherConfirmedTextLabel.setDisplayText(ExchangeMenuGUISection.EXCHANGE_CONFIRMED);
    }

    public void resetOtherOffer() {
        this.otherCurrencyOfferTextLabel.setDisplayText("0");
        this.offerButton.enable();
        this.cancelButton.disable();
        this.confirmButton.disable();
        this.clientConfirmedTextLabel.setDisplayText(ExchangeMenuGUISection.EXCHANGE_NOT_CONFIRMED);
        this.otherConfirmedTextLabel.setDisplayText(ExchangeMenuGUISection.EXCHANGE_NOT_CONFIRMED);  

        this.currencyField.enable();
    }
}
