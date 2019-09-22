package austeretony.oxygen_exchange.client.gui.exchange;

import java.io.IOException;

import austeretony.alternateui.container.core.AbstractGUIContainer;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.common.inventory.ExchangeMenuContainer;
import net.minecraft.item.ItemStack;

public class ExchangeMenuGUIContainer extends AbstractGUIContainer {

    private ExchangeGUISection exchangeSection;

    protected final ExchangeMenuContainer container;

    public ExchangeMenuGUIContainer(ExchangeMenuContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 172, 190);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.exchangeSection = new ExchangeGUISection(this));        
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

    public ExchangeGUISection getMainSection() {
        return this.exchangeSection;
    }

    public void madeOffer() {
        this.container.disableClientOfferSlots();

        this.exchangeSection.madeOffer();
    }

    public void canceledExchange() {
        for (int i = 0; i < 5; i++)
            this.container.inventorySlots.get(i).putStack(ItemStack.EMPTY);
        this.container.enableClientOfferSlots();     

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
        this.container.enableClientOfferSlots();

        this.exchangeSection.otherPlayerCanceledExchange();   
    }

    public void otherPlayerConfirmedExchange() {
        this.exchangeSection.otherPlayerConfirmedExchange();
    }
}
