package austeretony.oxygen_exchange.common;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.StatWatcherHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.EnumOxygenChatMessages;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.util.MathUtils;
import austeretony.oxygen.common.util.OxygenUtils;
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
        senderUUID = CommonReference.uuid(sender),
        targetUUID;
        if (OxygenHelperServer.isOnline(playerIndex)) {
            targetUUID = OxygenHelperServer.getSharedPlayerData(playerIndex).getPlayerUUID();
            if (!this.haveExchangeProcess(playerIndex)
                    && !OxygenHelperServer.isIgnored(targetUUID, senderUUID) 
                    && !senderUUID.equals(targetUUID)) {
                StatWatcherHelperServer.forceSync(senderUUID, OxygenMain.CURRENCY_GOLD_STAT_ID);
                StatWatcherHelperServer.forceSync(targetUUID, OxygenMain.CURRENCY_GOLD_STAT_ID);
                OxygenHelperServer.sendRequest(sender, CommonReference.playerByUUID(targetUUID), 
                        new ExchangeRequest(ExchangeMain.EXCHANGE_REQUEST_ID, senderUUID, CommonReference.username(sender)), true);
            } else
                OxygenHelperServer.sendMessage(sender, OxygenMain.OXYGEN_MOD_INDEX, EnumOxygenChatMessages.REQUEST_RESET.ordinal());
        }
    }

    public void processExchangeRequestReply(EntityPlayer target, UUID senderUUID) {
        if (OxygenHelperServer.isOnline(senderUUID)) {
            UUID targetUUID = CommonReference.uuid(target);
            EntityPlayerMP sender = CommonReference.playerByUUID(senderUUID);
            if (isPlayersNear(target, sender)) {
                long id = OxygenUtils.createDataStampedId();
                int 
                firstIndex = OxygenHelperServer.getPlayerIndex(senderUUID),
                secondIndex = OxygenHelperServer.getPlayerIndex(targetUUID);
                this.exchangeProcesses.put(id, new ExchangeProcess(senderUUID, firstIndex, targetUUID, secondIndex));
                this.access.put(firstIndex, id);
                this.access.put(secondIndex, id);
                openExchangeMenu(target);
                openExchangeMenu(sender);
            }
        }
    }

    public void processExchangeOperation(EntityPlayer player, EnumExchangeOperation operation, int offeredCurrency) {
        UUID playerUUID = CommonReference.uuid(player);
        int index = OxygenHelperServer.getPlayerIndex(playerUUID);
        if (this.haveExchangeProcess(index))
            this.getExchangeProcess(index).processAction(index, operation, 
                    MathUtils.clamp(offeredCurrency, 0, OxygenHelperServer.getPlayerData(playerUUID).getCurrency(OxygenPlayerData.CURRENCY_GOLD_INDEX)));
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

    public static boolean isPlayersNear(EntityPlayer firsPlayer, EntityPlayer secondPlayer) {
        return firsPlayer.getDistanceSq(secondPlayer) <= 25.0F;
    }

    public void runExchangeProcesses() {
        Iterator<ExchangeProcess> iterator = this.exchangeProcesses.values().iterator();
        ExchangeProcess exchangeProcess;
        while (iterator.hasNext()) {
            exchangeProcess = iterator.next();
            if (exchangeProcess.process()) {
                this.access.remove(exchangeProcess.firstPlayer.playerIndex);
                this.access.remove(exchangeProcess.secondPlayer.playerIndex);
                if (OxygenHelperServer.isOnline(exchangeProcess.firstPlayer.playerIndex))
                    CommonReference.playerByUUID(exchangeProcess.firstPlayer.playerUUID).closeScreen();
                if (OxygenHelperServer.isOnline(exchangeProcess.secondPlayer.playerIndex))
                    CommonReference.playerByUUID(exchangeProcess.secondPlayer.playerUUID).closeScreen();
                iterator.remove();
            }
        }
    }
    
    public void onPlayerUnloaded(EntityPlayer player) {         
        if (OxygenHelperServer.isOnline(CommonReference.uuid(player)))  
            this.processExchangeOperation(player, EnumExchangeOperation.CLOSE, 0);
    }
}
