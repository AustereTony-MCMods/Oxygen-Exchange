package austeretony.oxygen_exchange.client.gui.exchange;

import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_core.client.gui.elements.OxygenBackgroundFiller;

public class ExchangeMenuBackgroundFiller extends OxygenBackgroundFiller {

    public ExchangeMenuBackgroundFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height);
    }

    @Override
    public void drawBackground() {
        //main background  
        drawRect(0, 0, this.getWidth(), this.getHeight(), this.getEnabledBackgroundColor());      

        //title underline
        OxygenGUIUtils.drawRect(4.0D, 14.0D, this.getWidth() - 4.0D, 14.4D, this.getDisabledBackgroundColor());

        //slots underline
        OxygenGUIUtils.drawRect(4.0D, this.getHeight() - 12.6F, this.getWidth() - 4.0D, this.getHeight() - 13.0F, this.getDisabledBackgroundColor());
    }
}
