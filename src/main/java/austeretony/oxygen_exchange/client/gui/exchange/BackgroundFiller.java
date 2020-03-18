package austeretony.oxygen_exchange.client.gui.exchange;

import org.lwjgl.opengl.GL11;

import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_core.client.gui.elements.OxygenBackgroundFiller;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class BackgroundFiller extends OxygenBackgroundFiller {

    public BackgroundFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height);
    }

    @Override
    public void drawBackground() {
        //main background  
        drawRect(0, 0, this.getWidth(), this.getHeight(), this.getEnabledBackgroundColor());      

        //title underline
        OxygenGUIUtils.drawRect(4.0D, 14.0D, this.getWidth() - 4.0D, 14.4D, this.getDisabledBackgroundColor());

        //slots frame

        double 
        slotWidth = 18.0D,
        offersSlotsX = 6D,
        offersSlotsY = 31D;

        float 
        alpha = (float) (this.getDisabledBackgroundColor() >> 24 & 255) / 255.0F,
        red = (float) (this.getDisabledBackgroundColor() >> 16 & 255) / 255.0F,
        green = (float) (this.getDisabledBackgroundColor() >> 8 & 255) / 255.0F,
        blue = (float) (this.getDisabledBackgroundColor() & 255) / 255.0F;      

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);

        int i;

        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        for (i = 0; i < 6; i++) {
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY + slotWidth, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY + slotWidth, 0.0D).endVertex();
        }

        for (i = 0; i < 2; i++) {
            bufferBuilder.pos(offersSlotsX - 0.25D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * 5.0D + 0.5D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
        }

        offersSlotsY += 44.0D;

        for (i = 0; i < 6; i++) {
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY + slotWidth, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY + slotWidth, 0.0D).endVertex();
        }

        for (i = 0; i < 2; i++) {
            bufferBuilder.pos(offersSlotsX - 0.25D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * 5.0D + 0.5D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
        }

        offersSlotsY += 32.0D;

        for (i = 0; i < 10; i++) {
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY + slotWidth * 3.0D, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY + slotWidth * 3.0D, 0.0D).endVertex();
        }

        for (i = 0; i < 4; i++) {
            bufferBuilder.pos(offersSlotsX - 0.25D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * 9.0D + 0.5D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
        }

        offersSlotsY += 58.0D;

        for (i = 0; i < 10; i++) {
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i, offersSlotsY + slotWidth, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * i - 0.05D, offersSlotsY + slotWidth, 0.0D).endVertex();
        }

        for (i = 0; i < 2; i++) {
            bufferBuilder.pos(offersSlotsX - 0.25D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
            bufferBuilder.pos(offersSlotsX + slotWidth * 9.0D + 0.5D, offersSlotsY + slotWidth * i, 0.0D).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        //panel underline
        OxygenGUIUtils.drawRect(4.0D, this.getHeight() - 12.6D, this.getWidth() - 4.0D, this.getHeight() - 13.0D, this.getDisabledBackgroundColor());     

        //buttons background
        OxygenGUIUtils.drawRect(- this.screen.guiLeft, this.getHeight() + this.screen.guiTop - 10, this.mc.displayWidth, this.mc.displayHeight, this.getEnabledBackgroundColor());
    }
}
