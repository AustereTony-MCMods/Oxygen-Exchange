package austeretony.oxygen_exchange.client.gui.exchange;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;
import net.minecraft.client.renderer.GlStateManager;

public class ExchangeMenuBackgroundGUIFiller extends BackgroundGUIFiller {

    public ExchangeMenuBackgroundGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY) {  
        if (this.isVisible()) {         
            GlStateManager.pushMatrix();            
            GlStateManager.translate(this.getX(), this.getY(), 0.0F);            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);                      
            if (GUISettings.instance().shouldUseTextures()) {  
                GlStateManager.enableBlend();    
                this.mc.getTextureManager().bindTexture(ExchangeMenuGUIContainer.EXCHANGE_MENU_BACKGROUND_TEXTURE);                         
                GUIAdvancedElement.drawCustomSizedTexturedRect( - GUISettings.instance().getTextureOffsetX(), - GUISettings.instance().getTextureOffsetY(), 0, 0, this.textureWidth, this.textureHeight, this.textureWidth, this.textureHeight);             
                GlStateManager.disableBlend();   
            } else {
                drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
                drawRect(0, 0, this.getWidth(), 13, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
                drawRect(0, 14, 96, 64, GUISettings.instance().getAdditionalGUIBackgroundColor());//client offer background
                drawRect(97, 14, this.getWidth(), 64, GUISettings.instance().getAdditionalGUIBackgroundColor());//other offer background
                drawRect(0, 65, this.getWidth(), 77, GUISettings.instance().getAdditionalGUIBackgroundColor());//buttons background
                drawRect(0, 78, this.getWidth(), this.getHeight(), GUISettings.instance().getAdditionalGUIBackgroundColor());//inventory slots background
            }
            GlStateManager.popMatrix();            
        }
    }
}
