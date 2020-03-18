package austeretony.oxygen_exchange.client.gui.exchange;

import java.io.IOException;

import austeretony.alternateui.container.core.AbstractGUIContainer;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.settings.gui.EnumExchangeGUISetting;
import austeretony.oxygen_exchange.common.inventory.ContainerExchangeMenu;
import net.minecraft.item.ItemStack;

public class ExchangeMenuContainer extends AbstractGUIContainer {

    private ExchangeSection exchangeSection;

    protected final ContainerExchangeMenu container;

    public ExchangeMenuContainer(ContainerExchangeMenu container) {
        super(container);
        this.container = container;
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        EnumGUIAlignment alignment = EnumGUIAlignment.CENTER;
        switch (EnumExchangeGUISetting.EXCHANGE_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            alignment = EnumGUIAlignment.LEFT;
            break;
        case 0:
            alignment = EnumGUIAlignment.CENTER;
            break;
        case 1:
            alignment = EnumGUIAlignment.RIGHT;
            break;    
        default:
            alignment = EnumGUIAlignment.CENTER;
            break;
        }
        return new GUIWorkspace(this, 174, 197).setAlignment(alignment, 0, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.exchangeSection = new ExchangeSection(this));        
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.exchangeSection;
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
            ExchangeManagerClient.instance().getExchangeOperationsManager().closeExchangeMenuSynced();
        super.keyTyped(typedChar, keyCode);
    }

    public ExchangeSection getMainSection() {
        return this.exchangeSection;
    }

    public void madeOffer() {
        this.container.disableOfferSlots();

        this.exchangeSection.madeOffer();
    }

    public void canceledExchange() {
        for (int i = 0; i < 5; i++)
            this.container.inventorySlots.get(i).putStack(ItemStack.EMPTY);
        this.container.enableOfferSlots();     

        this.exchangeSection.canceledExchange();
    }

    public void confirmedExchange() {
        this.exchangeSection.confirmedExchange();
    }

    public void otherPlayerMadeOffer(long offeredCurrency, ItemStack[] offeredItems) {
        for (int i = 0; i < 5; i++)
            this.container.inventorySlots.get(i).putStack(offeredItems[i]);

        this.exchangeSection.otherPlayerMadeOffer(offeredCurrency, offeredItems);
    }

    public void otherPlayerCanceledExchange() {       
        for (int i = 0; i < 5; i++)
            this.container.inventorySlots.get(i).putStack(ItemStack.EMPTY);
        this.container.enableOfferSlots();

        this.exchangeSection.otherPlayerCanceledExchange();   
    }

    public void otherPlayerConfirmedExchange() {
        this.exchangeSection.otherPlayerConfirmedExchange();
    }
}
