package austeretony.oxygen_exchange.client.gui.exchange.callback;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.gui.exchange.ExchangeMenuGUIContainer;
import austeretony.oxygen_exchange.client.gui.exchange.ExchangeMenuGUISection;

public class ConfirmationGUICallback extends AbstractGUICallback {

    private final ExchangeMenuGUIContainer screen;

    private final ExchangeMenuGUISection section;

    private GUIButton confirmButton, cancelButton;

    public ConfirmationGUICallback(ExchangeMenuGUIContainer screen, ExchangeMenuGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new ConfirmationCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("oxygen_exchange.gui.exchange.confirmCallback"), true, GUISettings.instance().getTitleScale()));
        this.addElement(new GUITextLabel(2, 16).setDisplayText(ClientReference.localize("oxygen_exchange.gui.exchange.confirmCallback.request"), true, GUISettings.instance().getTextScale()));        

        this.addElement(this.confirmButton = new GUIButton(15, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()));
        this.addElement(this.cancelButton = new GUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.cancelButton) {
                this.close();
                this.section.enableConfirmButton();
            } else if (element == this.confirmButton) {
                ExchangeManagerClient.instance().confirmExchangeSynced();
                this.close();
            }
        }
    }
}
