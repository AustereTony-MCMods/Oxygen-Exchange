package austeretony.oxygen_exchange.client.gui.exchange;

import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;

public class ExchangeMenuGUIFiller extends BackgroundGUIFiller {

    public ExchangeMenuGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height, ExchangeMenuGUIContainer.EXCHANGE_MENU_BACKGROUND);
    }

    @Override
    public void drawDefaultBackground() {
        drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
        drawRect(0, 0, this.getWidth(), 13, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
        drawRect(0, 14, 96, 64, GUISettings.instance().getAdditionalGUIBackgroundColor());//client offer background
        drawRect(97, 14, this.getWidth(), 64, GUISettings.instance().getAdditionalGUIBackgroundColor());//other offer background
        drawRect(0, 65, this.getWidth(), 77, GUISettings.instance().getAdditionalGUIBackgroundColor());//buttons background
        drawRect(0, 78, this.getWidth(), this.getHeight(), GUISettings.instance().getAdditionalGUIBackgroundColor());//inventory slots background
    }
}
