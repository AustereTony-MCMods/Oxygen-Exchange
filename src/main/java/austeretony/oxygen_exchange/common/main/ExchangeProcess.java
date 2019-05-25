package austeretony.oxygen_exchange.common.main;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.SoundEventsHelperServer;
import austeretony.oxygen.common.api.StatWatcherHelperServer;
import austeretony.oxygen.common.config.OxygenConfig;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.common.util.PacketBufferUtils;
import austeretony.oxygen_exchange.common.network.client.CPExchangeCommand;
import austeretony.oxygen_exchange.common.network.client.CPSyncOtherPlayerOffer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.PacketBuffer;

public class ExchangeProcess {

    private final Queue<ExchangeOperation> operations = new ConcurrentLinkedQueue<ExchangeOperation>();

    public final ExchangeParticipant firstPlayer, secondPlayer;

    public ExchangeProcess(UUID firstPlayerUUID, int firstPlayerIndex, UUID secondPlayerUUID, int secondPlayerIndex) {
        this.firstPlayer = new ExchangeParticipant(firstPlayerUUID, firstPlayerIndex);
        this.secondPlayer = new ExchangeParticipant(secondPlayerUUID, secondPlayerIndex);
    }

    public void processAction(int playerIndex, EnumExchangeOperation operation, int offeredCurrency) {
        if (!OxygenHelperServer.isOnline(playerIndex == this.firstPlayer.playerIndex ? this.secondPlayer.playerIndex : this.firstPlayer.playerIndex))
            operation = EnumExchangeOperation.CLOSE;    
        this.operations.offer(new ExchangeOperation(playerIndex, operation, offeredCurrency));
    }

    public boolean process() {
        ExchangeOperation operation;
        while (!this.operations.isEmpty()) {
            operation = this.operations.poll();
            switch (operation.operation) {
            case OFFER:
                if (operation.playerIndex == this.firstPlayer.playerIndex) {
                    this.firstPlayer.setOfferedCurrency(operation.offeredCurrency);
                    for (int i = 5; i < 10; i++)
                        this.firstPlayer.offeredItems[i - 5] = CommonReference.playerByUUID(this.firstPlayer.playerUUID).openContainer.inventorySlots.get(i).getStack().copy();
                    this.syncOfferWith(this.secondPlayer.playerUUID, this.firstPlayer.offeredCurrency, this.firstPlayer.offeredItems);
                } else {
                    this.secondPlayer.setOfferedCurrency(operation.offeredCurrency);
                    for (int i = 5; i < 10; i++)
                        this.secondPlayer.offeredItems[i - 5] = CommonReference.playerByUUID(this.secondPlayer.playerUUID).openContainer.inventorySlots.get(i).getStack().copy();
                    this.syncOfferWith(this.firstPlayer.playerUUID, this.secondPlayer.offeredCurrency, this.secondPlayer.offeredItems);
                }
                break;
            case CANCEL:
                if (operation.playerIndex == this.firstPlayer.playerIndex) {
                    this.firstPlayer.setConfirmed(false);
                    this.secondPlayer.setConfirmed(false);
                    this.firstPlayer.resetOffer();
                    this.notifyOfferReset(this.secondPlayer.playerUUID);
                } else {
                    this.firstPlayer.setConfirmed(false);
                    this.secondPlayer.setConfirmed(false);
                    this.secondPlayer.resetOffer();
                    this.notifyOfferReset(this.firstPlayer.playerUUID);
                }
                break;
            case CONFIRM:
                if (operation.playerIndex == this.firstPlayer.playerIndex) {
                    this.firstPlayer.setConfirmed(true);

                    if (OxygenHelperServer.isOnline(this.secondPlayer.playerIndex))
                        this.notifyExchangeConfirmed(this.secondPlayer.playerUUID);
                    else
                        this.secondPlayer.setConfirmed(false);
                } else {
                    this.secondPlayer.setConfirmed(true);

                    if (OxygenHelperServer.isOnline(this.firstPlayer.playerIndex))
                        this.notifyExchangeConfirmed(this.firstPlayer.playerUUID);
                    else
                        this.firstPlayer.setConfirmed(false);
                }
                if (this.firstPlayer.confirmedExchange() 
                        && this.secondPlayer.confirmedExchange()) {
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

    private void syncOfferWith(UUID playerUUID, int offeredCurrency, ItemStack[] offeredItems) {
        if (OxygenHelperServer.isOnline(playerUUID))
            ExchangeMain.network().sendTo(new CPSyncOtherPlayerOffer(offeredCurrency, offeredItems), CommonReference.playerByUUID(playerUUID));
    }

    private void notifyOfferReset(UUID playerUUID) {
        if (OxygenHelperServer.isOnline(playerUUID))
            ExchangeMain.network().sendTo(new CPExchangeCommand(CPExchangeCommand.EnumCommand.SET_OTHER_RESET_OFFER), CommonReference.playerByUUID(playerUUID));
    }

    private void notifyExchangeConfirmed(UUID playerUUID) {
        ExchangeMain.network().sendTo(new CPExchangeCommand(CPExchangeCommand.EnumCommand.SET_OTHER_CONFIRMED_EXCHANGE), CommonReference.playerByUUID(playerUUID));
    }

    private void transferOffers() {
        EntityPlayerMP
        firstPlayer = CommonReference.playerByUUID(this.firstPlayer.playerUUID),
        secondPlayer = CommonReference.playerByUUID(this.secondPlayer.playerUUID);

        if (OxygenConfig.ENABLE_CURRENCY.getBooleanValue()) {
            OxygenPlayerData 
            firstData = OxygenHelperServer.getPlayerData(this.firstPlayer.playerUUID),
            secondData = OxygenHelperServer.getPlayerData(this.secondPlayer.playerUUID);

            if (this.firstPlayer.offeredCurrency > 0 || this.secondPlayer.offeredCurrency > 0) {
                firstData.removeCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.firstPlayer.offeredCurrency);
                secondData.removeCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.secondPlayer.offeredCurrency);

                firstData.addCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.secondPlayer.offeredCurrency);
                secondData.addCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX, this.firstPlayer.offeredCurrency);

                OxygenHelperServer.savePlayerDataDelegated(this.firstPlayer.playerUUID, firstData);
                OxygenHelperServer.savePlayerDataDelegated(this.secondPlayer.playerUUID, secondData);

                StatWatcherHelperServer.setValue(this.firstPlayer.playerUUID, OxygenMain.CURRENCY_GOLD_STAT_ID, firstData.getCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX));
                StatWatcherHelperServer.setValue(this.secondPlayer.playerUUID, OxygenMain.CURRENCY_GOLD_STAT_ID, secondData.getCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX));

                SoundEventsHelperServer.playSoundClient(firstPlayer, OxygenSoundEffects.SELL.id);
                SoundEventsHelperServer.playSoundClient(secondPlayer, OxygenSoundEffects.SELL.id);
            }
        }

        for (int i = 5; i < 10; i++) {//TODO Probably not safest way to remove items
            firstPlayer.openContainer.inventorySlots.get(i).putStack(ItemStack.EMPTY);
            secondPlayer.openContainer.inventorySlots.get(i).putStack(ItemStack.EMPTY);
        }

        for (ItemStack itemStack : this.secondPlayer.offeredItems)
            firstPlayer.inventory.addItemStackToInventory(itemStack);
        for (ItemStack itemStack : this.firstPlayer.offeredItems)
            secondPlayer.inventory.addItemStackToInventory(itemStack);

        firstPlayer.openContainer.detectAndSendChanges();
        secondPlayer.openContainer.detectAndSendChanges();

        SoundEventsHelperServer.playSoundClient(firstPlayer, OxygenSoundEffects.INVENTORY.id);
        SoundEventsHelperServer.playSoundClient(secondPlayer, OxygenSoundEffects.INVENTORY.id);
    }

    public static void writeItemStack(ItemStack itemStack, PacketBuffer buffer) {
        buffer.writeInt(Item.getIdFromItem(itemStack.getItem()));
        buffer.writeByte(itemStack.getCount());
        buffer.writeByte(itemStack.getMetadata());
        buffer.writeShort(itemStack.getItemDamage());
        PacketBufferUtils.writeString(itemStack.hasTagCompound() ? itemStack.getTagCompound().toString() : "", buffer);
    }

    public static ItemStack readItemStack(PacketBuffer buffer) {
        ItemStack itemStack = new ItemStack(Item.getItemById(buffer.readInt()), buffer.readByte(), buffer.readByte());
        itemStack.setItemDamage(buffer.readShort());
        String nbtStr = PacketBufferUtils.readString(buffer);
        try {
            if (!nbtStr.isEmpty())
                itemStack.setTagCompound(JsonToNBT.getTagFromJson(nbtStr));
        } catch (NBTException exception) {
            ExchangeMain.LOGGER.error("ItemStack {} NBT parsing failure!", itemStack.toString());
            exception.printStackTrace();
        }
        return itemStack;
    }

    public static class ExchangeParticipant {

        public final UUID playerUUID;

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

        public ExchangeParticipant(UUID playerUUID, int playerIndex) {
            this.playerUUID = playerUUID;
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
