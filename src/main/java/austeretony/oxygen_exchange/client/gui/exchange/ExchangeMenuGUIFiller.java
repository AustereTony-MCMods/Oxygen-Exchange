package austeretony.oxygen_exchange.client.gui.exchange;

import austeretony.oxygen_core.client.gui.elements.BackgroundGUIFiller;
import austeretony.oxygen_core.client.gui.elements.CustomRectUtils;

public class ExchangeMenuGUIFiller extends BackgroundGUIFiller {

    public ExchangeMenuGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height);
    }

    @Override
    public void drawBackground() {
        //main background  
        drawRect(0, 0, this.getWidth(), this.getHeight(), this.getEnabledBackgroundColor());      

        //title underline
        CustomRectUtils.drawRect(4.0D, 14.0D, this.getWidth() - 4.0D, 14.4D, this.getDisabledBackgroundColor());
        
        CustomRectUtils.drawRect(4.0D, 100.0D, this.getWidth() - 4.0D, 100.4D, this.getDisabledBackgroundColor());   
        CustomRectUtils.drawRect(6.0D, 156.0D, this.getWidth() - 6.0D, 156.4D, this.getDisabledBackgroundColor());
        CustomRectUtils.drawRect(4.0D, 176.0D, this.getWidth() - 4.0D, 176.4D, this.getDisabledBackgroundColor());
    }
}
