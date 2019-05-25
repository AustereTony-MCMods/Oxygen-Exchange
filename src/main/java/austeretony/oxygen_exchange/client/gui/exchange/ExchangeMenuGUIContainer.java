package austeretony.oxygen_exchange.client.gui.exchange;

import java.io.IOException;

import austeretony.alternateui.container.core.AbstractGUIContainer;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.common.inventory.ExchangeMenuContainer;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import net.minecraft.util.ResourceLocation;

public class ExchangeMenuGUIContainer extends AbstractGUIContainer {

    public static final ResourceLocation EXCHANGE_MENU_BACKGROUND_TEXTURE = new ResourceLocation(ExchangeMain.MODID, "textures/gui/menu/exchange_menu_background.png");

    private ExchangeMenuGUISection mainSection;

    public final ExchangeMenuContainer container;

    public ExchangeMenuGUIContainer(ExchangeMenuContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 193, 154);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.mainSection = new ExchangeMenuGUISection(this));        
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.mainSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1)
            ExchangeManagerClient.instance().closeExchangeMenuSynced();
        super.keyTyped(typedChar, keyCode);
    }

    public ExchangeMenuGUISection getMainSection() {
        return this.mainSection;
    }
}
