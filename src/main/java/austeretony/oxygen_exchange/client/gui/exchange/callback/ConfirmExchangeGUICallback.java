package austeretony.oxygen_exchange.client.gui.exchange.callback;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.gui.exchange.ExchangeMenuGUIContainer;
import austeretony.oxygen_exchange.client.gui.exchange.ExchangeMenuGUISection;
import net.minecraft.client.resources.I18n;

public class ConfirmExchangeGUICallback extends AbstractGUICallback {

    private final ExchangeMenuGUIContainer screen;

    private final ExchangeMenuGUISection section;

    private GUIButton confirmButton, cancelButton;

    public ConfirmExchangeGUICallback(ExchangeMenuGUIContainer screen, ExchangeMenuGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new GUIImageLabel(- 1, - 1, this.getWidth() + 2, this.getHeight() + 2).enableStaticBackground(GUISettings.instance().getBaseGUIBackgroundColor()));//main background 1st layer
        this.addElement(new GUIImageLabel(0, 0, this.getWidth(), 11).enableStaticBackground(GUISettings.instance().getAdditionalGUIBackgroundColor()));//main background 2nd layer
        this.addElement(new GUIImageLabel(0, 12, this.getWidth(), this.getHeight() - 12).enableStaticBackground(GUISettings.instance().getAdditionalGUIBackgroundColor()));//main background 2nd layer
        this.addElement(new GUITextLabel(2, 2).setDisplayText(I18n.format("oxygen_exchange.gui.exchange.confirmCallback"), true, GUISettings.instance().getTitleScale()));
        this.addElement(new GUITextLabel(2, 16).setDisplayText(I18n.format("oxygen_exchange.gui.exchange.confirmCallback.request"), true, GUISettings.instance().getTextScale()));        

        this.addElement(this.confirmButton = new GUIButton(15, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(I18n.format("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()));
        this.addElement(this.cancelButton = new GUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(I18n.format("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (element == this.cancelButton) {
            this.close();
            this.section.enableConfirmButton();
        } else if (element == this.confirmButton) {
            ExchangeManagerClient.instance().confirmExchangeSynced();
            this.close();
        }
    }
}
