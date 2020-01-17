package austeretony.oxygen_exchange.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_exchange.client.gui.exchange.ExchangeMenuContainer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;

public class ExchangeMenuManager {

    public void madeOffer() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((ExchangeMenuContainer) ClientReference.getCurrentScreen()).madeOffer();
        });
    }

    public void canceledExchange() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((ExchangeMenuContainer) ClientReference.getCurrentScreen()).canceledExchange();
        });
    }

    public void confirmedExchange() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((ExchangeMenuContainer) ClientReference.getCurrentScreen()).confirmedExchange();
        });
    }

    public void otherPlayerMadeOffer(long offeredCurrency, byte[] compressedItems) {
        final ItemStack[] offeredItems = new ItemStack[5];
        for (int i = 0; i < 5; i++)
            offeredItems[i] = ItemStack.EMPTY;
        ByteBuf buffer = null;
        try {
            buffer = Unpooled.wrappedBuffer(compressedItems);
            for (int i = 0; i < 5; i++)
                offeredItems[i] = ByteBufUtils.readItemStack(buffer);
        } finally {
            if (buffer != null)
                buffer.release();
        }
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((ExchangeMenuContainer) ClientReference.getCurrentScreen()).otherPlayerMadeOffer(offeredCurrency, offeredItems);
        });
    }

    public void otherPlayerCanceledExchange() {       
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((ExchangeMenuContainer) ClientReference.getCurrentScreen()).otherPlayerCanceledExchange();
        });
    }

    public void otherPlayerConfirmedExchange() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((ExchangeMenuContainer) ClientReference.getCurrentScreen()).otherPlayerConfirmedExchange();
        });
    }

    public static boolean isMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof ExchangeMenuContainer;
    }
}
