package austeretony.oxygen_exchange.common.main;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.SoundEventHelperServer;
import austeretony.oxygen.common.api.WatcherHelperServer;
import austeretony.oxygen.common.config.OxygenConfig;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_exchange.common.inventory.ExchangeMenuContainer;
import austeretony.oxygen_exchange.common.network.client.CPExchangeCommand;
import austeretony.oxygen_exchange.common.network.client.CPSyncOtherPlayerOffer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ExchangeProcess {

    private final Queue<ExchangeOperation> operations = new ConcurrentLinkedQueue<ExchangeOperation>();

    public final ExchangeParticipant firstParticipant, secondParticipant;

    public ExchangeProcess(EntityPlayerMP firstPlayer, int firstPlayerIndex, EntityPlayerMP secondPlayer, int secondPlayerIndex) {
        this.firstParticipant = new ExchangeParticipant(firstPlayer, firstPlayerIndex);
        this.secondParticipant = new ExchangeParticipant(secondPlayer, secondPlayerIndex);
    }

    public void processAction(int playerIndex, EnumExchangeOperation operation, int offeredCurrency) {
        if (operation != EnumExchangeOperation.CLOSE 
                && !OxygenHelperServer.isOnline(playerIndex == this.firstParticipant.playerIndex ? this.secondParticipant.playerIndex : this.firstParticipant.playerIndex))
            operation = EnumExchangeOperation.CLOSE;    
        this.operations.offer(new ExchangeOperation(playerIndex, operation, offeredCurrency));
    }

    public boolean process() {
        ExchangeOperation operation;
        Container container;
        while (!this.operations.isEmpty()) {
            operation = this.operations.poll();
            switch (operation.operation) {
            case OFFER:
                if (operation.playerIndex == this.firstParticipant.playerIndex) {
                    this.firstParticipant.setOfferedCurrency(operation.offeredCurrency);

                    container = this.firstParticipant.player.openContainer;
                    if (this.disableOfferSlots(container))
                        return true;

                    for (int i = 5; i < 10; i++)
                        this.firstParticipant.offeredItems[i - 5] = container.inventorySlots.get(i).getStack().copy();

                    this.syncOfferWith(this.secondParticipant.player, this.firstParticipant.offeredCurrency, this.firstParticipant.offeredItems);
                } else {
                    this.secondParticipant.setOfferedCurrency(operation.offeredCurrency);

                    container = this.secondParticipant.player.openContainer;
                    if (this.disableOfferSlots(container))
                        return true;

                    for (int i = 5; i < 10; i++)
                        this.secondParticipant.offeredItems[i - 5] = container.inventorySlots.get(i).getStack().copy();

                    this.syncOfferWith(this.firstParticipant.player, this.secondParticipant.offeredCurrency, this.secondParticipant.offeredItems);
                }
                break;
            case CANCEL:
                if (operation.playerIndex == this.firstParticipant.playerIndex) {
                    this.firstParticipant.setConfirmed(false);
                    this.secondParticipant.setConfirmed(false);
                    this.firstParticipant.resetOffer();

                    container = this.firstParticipant.player.openContainer;
                    if (this.enbleOfferSlots(container))
                        return true;
                    container = this.secondParticipant.player.openContainer;
                    if (this.enbleOfferSlots(container))
                        return true;

                    this.notifyOfferReset(this.secondParticipant.player);
                } else {
                    this.firstParticipant.setConfirmed(false);
                    this.secondParticipant.setConfirmed(false);
                    this.secondParticipant.resetOffer();

                    container = this.firstParticipant.player.openContainer;
                    if (this.enbleOfferSlots(container))
                        return true;
                    container = this.secondParticipant.player.openContainer;
                    if (this.enbleOfferSlots(container))
                        return true;

                    this.notifyOfferReset(this.firstParticipant.player);
                }
                break;
            case CONFIRM:
                if (operation.playerIndex == this.firstParticipant.playerIndex) {
                    this.firstParticipant.setConfirmed(true);
                    this.notifyExchangeConfirmed(this.secondParticipant.player);
                } else {
                    this.secondParticipant.setConfirmed(true);
                    this.notifyExchangeConfirmed(this.firstParticipant.player);
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
        return false;
    }

    private boolean disableOfferSlots(Container container) {
        if (!(container instanceof ExchangeMenuContainer))
            return true;
        ((ExchangeMenuContainer) container).disableClientOfferSlots();
        return false;
    }

    private boolean enbleOfferSlots(Container container) {
        if (!(container instanceof ExchangeMenuContainer))
            return true;
        ((ExchangeMenuContainer) container).enableClientOfferSlots();
        return false;
    }

    private void syncOfferWith(EntityPlayerMP player, int offeredCurrency, ItemStack[] offeredItems) {
        ExchangeMain.network().sendTo(new CPSyncOtherPlayerOffer(offeredCurrency, offeredItems), player);
    }

    private void notifyOfferReset(EntityPlayerMP player) {
        ExchangeMain.network().sendTo(new CPExchangeCommand(CPExchangeCommand.EnumCommand.SET_OTHER_RESET_OFFER), player);
    }

    private void notifyExchangeConfirmed(EntityPlayerMP player) {
        ExchangeMain.network().sendTo(new CPExchangeCommand(CPExchangeCommand.EnumCommand.SET_OTHER_CONFIRMED_EXCHANGE), player);
    }

    private void transferOffers() {
        if (OxygenConfig.ENABLE_CURRENCY.getBooleanValue()) {
            UUID 
            firstUUID = CommonReference.getPersistentUUID(this.firstParticipant.player),
            secondUUID = CommonReference.getPersistentUUID(this.secondParticipant.player);
            OxygenPlayerData 
            firstData = OxygenHelperServer.getPlayerData(firstUUID),
            secondData = OxygenHelperServer.getPlayerData(secondUUID);

            if (this.firstParticipant.offeredCurrency > 0 || this.secondParticipant.offeredCurrency > 0) {
                firstData.removeCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.firstParticipant.offeredCurrency);
                secondData.removeCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.secondParticipant.offeredCurrency);

                firstData.addCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.secondParticipant.offeredCurrency);
                secondData.addCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.firstParticipant.offeredCurrency);

                OxygenHelperServer.savePersistentDataDelegated(firstData);
                OxygenHelperServer.savePersistentDataDelegated(secondData);

                WatcherHelperServer.setValue(firstUUID, OxygenPlayerData.CURRENCY_GOLD_ID, firstData.getCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX));
                WatcherHelperServer.setValue(secondUUID, OxygenPlayerData.CURRENCY_GOLD_ID, secondData.getCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX));

                SoundEventHelperServer.playSoundClient(this.firstParticipant.player, OxygenSoundEffects.SELL.id);
                SoundEventHelperServer.playSoundClient(this.secondParticipant.player, OxygenSoundEffects.SELL.id);
            }
        }

        for (int i = 5; i < 10; i++) {//TODO Probably not safest way to remove items
            this.firstParticipant.player.openContainer.inventorySlots.get(i).putStack(ItemStack.EMPTY);
            this.secondParticipant.player.openContainer.inventorySlots.get(i).putStack(ItemStack.EMPTY);
        }

        for (ItemStack itemStack : this.secondParticipant.offeredItems)
            this.firstParticipant.player.inventory.addItemStackToInventory(itemStack);
        for (ItemStack itemStack : this.firstParticipant.offeredItems)
            this.secondParticipant.player.inventory.addItemStackToInventory(itemStack);

        this.firstParticipant.player.openContainer.detectAndSendChanges();
        this.secondParticipant.player.openContainer.detectAndSendChanges();

        SoundEventHelperServer.playSoundClient(this.firstParticipant.player, OxygenSoundEffects.INVENTORY.id);
        SoundEventHelperServer.playSoundClient(this.secondParticipant.player, OxygenSoundEffects.INVENTORY.id);
    }

    public static class ExchangeParticipant {

        public final EntityPlayerMP player;

        public final int playerIndex;

        public final ItemStack[] offeredItems = new ItemStack[] {
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        };

        private int offeredCurrency;

        private boolean confirmed;

        public ExchangeParticipant(EntityPlayerMP player, int playerIndex) {
            this.player = player;
            this.playerIndex = playerIndex;
        }

        public int getOfferedCurrency() {
            return this.offeredCurrency;
        }

        public void setOfferedCurrency(int value) {
            this.offeredCurrency = value;
        }

        public boolean confirmedExchange() {
            return this.confirmed;
        }

        public void setConfirmed(boolean flag) {
            this.confirmed = flag;
        }

        public void resetOffer() {
            this.offeredCurrency = 0;
            for (int i = 0; i < 5; i++)
                this.offeredItems[i] = ItemStack.EMPTY;
        }
    }

    public static class ExchangeOperation {

        public final int playerIndex;

        public final EnumExchangeOperation operation;

        public final int offeredCurrency;

        public ExchangeOperation(int playerIndex, EnumExchangeOperation operation, int offeredCurrency) {
            this.playerIndex = playerIndex;
            this.operation = operation;
            this.offeredCurrency = offeredCurrency;
        }
    }

    public enum EnumExchangeOperation {

        OFFER,
        CANCEL,
        CONFIRM,
        CLOSE
    }
}
