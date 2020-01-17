package austeretony.oxygen_exchange.server;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.CurrencyHelperServer;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.SoundEventHelperServer;
import austeretony.oxygen_exchange.common.EnumExchangeOperation;
import austeretony.oxygen_exchange.common.inventory.ContainerExchangeMenu;
import austeretony.oxygen_exchange.common.main.EnumExchangeStatusMessage;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import austeretony.oxygen_exchange.common.network.client.CPExchangeOperation;
import austeretony.oxygen_exchange.common.network.client.CPSyncOtherPlayerOffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ExchangeProcess {

    private final Queue<QueuedExchangeOperation> operations = new ConcurrentLinkedQueue<>();

    public final ExchangeParticipant firstParticipant, secondParticipant;

    public ExchangeProcess(EntityPlayerMP firstPlayer, int firstPlayerIndex, EntityPlayerMP secondPlayer, int secondPlayerIndex) {
        this.firstParticipant = new ExchangeParticipant(firstPlayer, firstPlayerIndex);
        this.secondParticipant = new ExchangeParticipant(secondPlayer, secondPlayerIndex);
    }

    public void processAction(int playerIndex, EnumExchangeOperation operation, long offeredCurrency) {
        if (operation != EnumExchangeOperation.CLOSE) {
            if (!OxygenHelperServer.isPlayerOnline(this.firstParticipant.playerIndex) || !OxygenHelperServer.isPlayerOnline(this.secondParticipant.playerIndex))
                operation = EnumExchangeOperation.CLOSE;    
        }
        this.operations.offer(new QueuedExchangeOperation(playerIndex, operation, offeredCurrency));
    }

    public boolean process() {
        QueuedExchangeOperation operation;
        Container container;
        while (!this.operations.isEmpty()) {
            operation = this.operations.poll();
            if (operation != null) {
                switch (operation.operation) {
                case OFFER:
                    if (operation.playerIndex == this.firstParticipant.playerIndex) {
                        this.firstParticipant.setOfferedCurrency(operation.offeredCurrency);

                        container = this.firstParticipant.player.openContainer;
                        if (this.disableOfferSlots(container))
                            return true;

                        for (int i = 5; i < 10; i++)
                            this.firstParticipant.offeredItems[i - 5] = container.inventorySlots.get(i).getStack().copy();

                        if (this.validateOfferedItems(this.firstParticipant.offeredItems)) {
                            this.notifyPlayer(this.firstParticipant.player, CPExchangeOperation.EnumOperation.OFFERED);
                            this.syncOfferWith(this.secondParticipant.player, this.firstParticipant.offeredCurrency, this.firstParticipant.offeredItems);
                        } else
                            OxygenHelperServer.sendStatusMessage(this.firstParticipant.player, ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeStatusMessage.ITEM_BLACKLISTED.ordinal());
                    } else {
                        this.secondParticipant.setOfferedCurrency(operation.offeredCurrency);

                        container = this.secondParticipant.player.openContainer;
                        if (this.disableOfferSlots(container))
                            return true;

                        for (int i = 5; i < 10; i++)
                            this.secondParticipant.offeredItems[i - 5] = container.inventorySlots.get(i).getStack().copy();

                        if (this.validateOfferedItems(this.secondParticipant.offeredItems)) {
                            this.notifyPlayer(this.secondParticipant.player, CPExchangeOperation.EnumOperation.OFFERED);
                            this.syncOfferWith(this.firstParticipant.player, this.secondParticipant.offeredCurrency, this.secondParticipant.offeredItems);
                        } else
                            OxygenHelperServer.sendStatusMessage(this.secondParticipant.player, ExchangeMain.EXCHANGE_MOD_INDEX, EnumExchangeStatusMessage.ITEM_BLACKLISTED.ordinal());
                    }
                    break;
                case CANCEL:
                    if (operation.playerIndex == this.firstParticipant.playerIndex) {
                        this.firstParticipant.setConfirmed(false);
                        this.secondParticipant.setConfirmed(false);
                        this.firstParticipant.resetOffer();
                        this.secondParticipant.resetOffer();

                        container = this.firstParticipant.player.openContainer;
                        if (this.enableOfferSlots(container))
                            return true;
                        container = this.secondParticipant.player.openContainer;
                        if (this.enableOfferSlots(container))
                            return true;

                        this.notifyPlayer(this.firstParticipant.player, CPExchangeOperation.EnumOperation.CANCELED);
                        this.notifyPlayer(this.secondParticipant.player, CPExchangeOperation.EnumOperation.OTHER_CANCELED);
                    } else {
                        this.firstParticipant.setConfirmed(false);
                        this.secondParticipant.setConfirmed(false);
                        this.firstParticipant.resetOffer();
                        this.secondParticipant.resetOffer();

                        container = this.firstParticipant.player.openContainer;
                        if (this.enableOfferSlots(container))
                            return true;
                        container = this.secondParticipant.player.openContainer;
                        if (this.enableOfferSlots(container))
                            return true;

                        this.notifyPlayer(this.secondParticipant.player, CPExchangeOperation.EnumOperation.CANCELED);
                        this.notifyPlayer(this.firstParticipant.player, CPExchangeOperation.EnumOperation.OTHER_CANCELED);
                    }
                    break;
                case CONFIRM:
                    if (operation.playerIndex == this.firstParticipant.playerIndex) {
                        this.firstParticipant.setConfirmed(true);
                        this.notifyPlayer(this.firstParticipant.player, CPExchangeOperation.EnumOperation.CONFIRMED);
                        this.notifyPlayer(this.secondParticipant.player, CPExchangeOperation.EnumOperation.OTHER_CONFIRMED);
                    } else {
                        this.secondParticipant.setConfirmed(true);
                        this.notifyPlayer(this.secondParticipant.player, CPExchangeOperation.EnumOperation.CONFIRMED);
                        this.notifyPlayer(this.firstParticipant.player, CPExchangeOperation.EnumOperation.OTHER_CONFIRMED);
                    }
                    if (this.firstParticipant.confirmedExchange() 
                            && this.secondParticipant.confirmedExchange()) {
                        this.transferOffers();
                        return true;
                    }
                    break;
                case CLOSE:
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateOfferedItems(ItemStack[] offeredItems) {
        for (ItemStack itemStack : offeredItems)
            if (ExchangeManagerServer.instance().getItemsBlacklist().isBlackListed(itemStack))
                return false;
        return true;
    }

    private boolean disableOfferSlots(Container container) {
        if (!(container instanceof ContainerExchangeMenu))
            return true;
        ((ContainerExchangeMenu) container).disableOfferSlots();
        return false;
    }

    private boolean enableOfferSlots(Container container) {
        if (!(container instanceof ContainerExchangeMenu))
            return true;
        ((ContainerExchangeMenu) container).enableOfferSlots();
        return false;
    }

    private void syncOfferWith(EntityPlayerMP playerMP, long offeredCurrency, ItemStack[] offeredItems) {
        ByteBuf buffer = null;
        try {
            buffer = Unpooled.buffer();
            for (ItemStack itemStack : offeredItems)
                ByteBufUtils.writeItemStack(itemStack, buffer);
            byte[] compressedItems = new byte[buffer.writerIndex()];
            buffer.readBytes(compressedItems);
            OxygenMain.network().sendTo(new CPSyncOtherPlayerOffer(offeredCurrency, compressedItems), playerMP);
        } finally {
            if (buffer != null)
                buffer.release();
        }
    }

    private void notifyPlayer(EntityPlayerMP playerMP, CPExchangeOperation.EnumOperation operation) {
        OxygenMain.network().sendTo(new CPExchangeOperation(operation), playerMP);
    }

    private void transferOffers() {
        if (this.firstParticipant.offeredCurrency > 0L || this.secondParticipant.offeredCurrency > 0L) {
            UUID 
            firstUUID = CommonReference.getPersistentUUID(this.firstParticipant.player),
            secondUUID = CommonReference.getPersistentUUID(this.secondParticipant.player);

            if (this.firstParticipant.offeredCurrency > 0L) {
                CurrencyHelperServer.removeCurrency(firstUUID, this.firstParticipant.offeredCurrency, OxygenMain.COMMON_CURRENCY_INDEX);
                CurrencyHelperServer.addCurrency(secondUUID, this.firstParticipant.offeredCurrency, OxygenMain.COMMON_CURRENCY_INDEX);
            }

            if (this.secondParticipant.offeredCurrency > 0L) {
                CurrencyHelperServer.removeCurrency(secondUUID, this.secondParticipant.offeredCurrency, OxygenMain.COMMON_CURRENCY_INDEX);
                CurrencyHelperServer.addCurrency(firstUUID, this.secondParticipant.offeredCurrency, OxygenMain.COMMON_CURRENCY_INDEX);
            }

            SoundEventHelperServer.playSoundClient(this.firstParticipant.player, OxygenSoundEffects.SELL.id);
            SoundEventHelperServer.playSoundClient(this.secondParticipant.player, OxygenSoundEffects.SELL.id);
        }

        for (int i = 5; i < 10; i++) {
            this.firstParticipant.player.openContainer.inventorySlots.get(i).putStack(ItemStack.EMPTY);
            this.secondParticipant.player.openContainer.inventorySlots.get(i).putStack(ItemStack.EMPTY);
        }

        boolean itemsAdded = false;
        for (ItemStack itemStack : this.secondParticipant.offeredItems) {
            if (!itemStack.isEmpty())
                itemsAdded = true;
            CommonReference.delegateToServerThread(()->this.firstParticipant.player.inventory.addItemStackToInventory(itemStack));
        }
        if (itemsAdded)
            SoundEventHelperServer.playSoundClient(this.firstParticipant.player, OxygenSoundEffects.INVENTORY.id);

        itemsAdded = false;
        for (ItemStack itemStack : this.firstParticipant.offeredItems) {
            if (itemStack.isEmpty())
                itemsAdded = true;
            CommonReference.delegateToServerThread(()->this.secondParticipant.player.inventory.addItemStackToInventory(itemStack));
        }
        if (itemsAdded)
            SoundEventHelperServer.playSoundClient(this.secondParticipant.player, OxygenSoundEffects.INVENTORY.id);

        this.firstParticipant.player.openContainer.detectAndSendChanges();
        this.secondParticipant.player.openContainer.detectAndSendChanges();
    }

    static class ExchangeParticipant {

        public final EntityPlayerMP player;

        public final int playerIndex;

        public final ItemStack[] offeredItems = new ItemStack[5];

        private long offeredCurrency;

        private boolean confirmed;

        ExchangeParticipant(EntityPlayerMP player, int playerIndex) {
            this.player = player;
            this.playerIndex = playerIndex;
            this.resetOffer();
        }

        public long getOfferedCurrency() {
            return this.offeredCurrency;
        }

        public void setOfferedCurrency(long value) {
            this.offeredCurrency = value;
        }

        public boolean confirmedExchange() {
            return this.confirmed;
        }

        public void setConfirmed(boolean flag) {
            this.confirmed = flag;
        }

        public void resetOffer() {
            this.offeredCurrency = 0L;
            for (int i = 0; i < 5; i++)
                this.offeredItems[i] = ItemStack.EMPTY;
        }
    }
}
