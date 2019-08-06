package austeretony.oxygen_exchange.common;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.WatcherHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.EnumOxygenChatMessage;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen.util.OxygenUtils;
import austeretony.oxygen_exchange.common.main.ExchangeGUIHandler;
import austeretony.oxygen_exchange.common.main.ExchangeMain;
import austeretony.oxygen_exchange.common.main.ExchangeProcess;
import austeretony.oxygen_exchange.common.main.ExchangeProcess.EnumExchangeOperation;
import austeretony.oxygen_exchange.common.main.ExchangeRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ExchangeManagerServer {

    private static ExchangeManagerServer instance;

    private final Map<Long, ExchangeProcess> exchangeProcesses = new ConcurrentHashMap<Long, ExchangeProcess>();

    private final Map<Integer, Long> access = new ConcurrentHashMap<Integer, Long>();

    public static void create() {
        if (instance == null)
            instance = new ExchangeManagerServer();
    }

    public static ExchangeManagerServer instance() {
        return instance;
    }

    public void sendExchangeRequest(EntityPlayer sender, int playerIndex) {
        UUID 
        senderUUID = CommonReference.getPersistentUUID(sender),
        targetUUID;
        if (OxygenHelperServer.isOnline(playerIndex)) {
            targetUUID = OxygenHelperServer.getSharedPlayerData(playerIndex).getPlayerUUID();
            EntityPlayerMP target = CommonReference.playerByUUID(targetUUID);
            if (CommonReference.isEntitiesNear(sender, target, 5.0D)
                    && !this.haveExchangeProcess(playerIndex)
                    && !senderUUID.equals(targetUUID)) {
                WatcherHelperServer.forceSync(senderUUID, OxygenPlayerData.CURRENCY_COINS_WATCHER_ID);
                WatcherHelperServer.forceSync(targetUUID, OxygenPlayerData.CURRENCY_COINS_WATCHER_ID);
                OxygenHelperServer.sendRequest(sender, target, 
                        new ExchangeRequest(ExchangeMain.EXCHANGE_REQUEST_ID, senderUUID, CommonReference.getName(sender)), true);
            } else
                OxygenHelperServer.sendMessage(sender, OxygenMain.OXYGEN_MOD_INDEX, EnumOxygenChatMessage.REQUEST_RESET.ordinal());
        }
    }

    public void processExchangeRequestReply(EntityPlayer target, UUID senderUUID) {
        if (OxygenHelperServer.isOnline(senderUUID)) {
            UUID targetUUID = CommonReference.getPersistentUUID(target);
            EntityPlayerMP sender = CommonReference.playerByUUID(senderUUID);
            if (CommonReference.isEntitiesNear(sender, target, 5.0D)) {
                long id = OxygenUtils.createDataStampedId();
                int 
                firstIndex = OxygenHelperServer.getPlayerIndex(senderUUID),
                secondIndex = OxygenHelperServer.getPlayerIndex(targetUUID);
                this.exchangeProcesses.put(id, new ExchangeProcess(sender, firstIndex, (EntityPlayerMP) target, secondIndex));
                this.access.put(firstIndex, id);
                this.access.put(secondIndex, id);
                openExchangeMenu(target);
                openExchangeMenu(sender);
            }
        }
    }

    public void processExchangeOperation(EntityPlayer player, EnumExchangeOperation operation, int offeredCurrency) {
        UUID playerUUID = CommonReference.getPersistentUUID(player);
        int index = OxygenHelperServer.getPlayerIndex(playerUUID);
        if (this.haveExchangeProcess(index))
            this.getExchangeProcess(index).processAction(index, operation, 
                    MathUtils.clamp(offeredCurrency, 0, OxygenHelperServer.getPlayerData(playerUUID).getCurrency(OxygenPlayerData.CURRENCY_COINS_INDEX)));
    }

    public boolean haveExchangeProcess(int index) {
        return this.access.containsKey(index);   
    }

    public ExchangeProcess getExchangeProcess(int index) {
        return this.exchangeProcesses.get(this.access.get(index));
    }

    public static void openExchangeMenu(EntityPlayer player) {
        player.openGui(ExchangeMain.instance, ExchangeGUIHandler.EXCHANGE_MENU, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);                            
    }

    public void runExchangeProcesses() {
        Iterator<ExchangeProcess> iterator = this.exchangeProcesses.values().iterator();
        ExchangeProcess exchangeProcess;
        while (iterator.hasNext()) {
            exchangeProcess = iterator.next();
            if (exchangeProcess.process()) {
                this.access.remove(exchangeProcess.firstParticipant.playerIndex);
                this.access.remove(exchangeProcess.secondParticipant.playerIndex);
                if (OxygenHelperServer.isOnline(exchangeProcess.firstParticipant.playerIndex))
                    exchangeProcess.firstParticipant.player.closeScreen();
                if (OxygenHelperServer.isOnline(exchangeProcess.secondParticipant.playerIndex))
                    exchangeProcess.secondParticipant.player.closeScreen();
                iterator.remove();
            }
        }
    }

    public void onPlayerUnloaded(EntityPlayer player) {       
        int index = OxygenHelperServer.getPlayerIndex(CommonReference.getPersistentUUID(player));
        if (this.haveExchangeProcess(index))
            this.getExchangeProcess(index).processAction(index, EnumExchangeOperation.CLOSE, 0);
    }
}
