package austeretony.oxygen_exchange.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.item.ItemsBlackList;
import net.minecraft.entity.player.EntityPlayerMP;

public class ExchangeManagerServer {

    private static ExchangeManagerServer instance;

    private final ExchangeProcessesManagerServer exchangeProcessesManager = new ExchangeProcessesManagerServer();

    private final ItemsBlackList itemsBlacklist = ItemsBlackList.create("exchange");

    private void scheduleRepeatableProcesses() {
        OxygenManagerServer.instance().getExecutionManager().getExecutors().getSchedulerExecutorService().scheduleAtFixedRate(
                ()->this.exchangeProcessesManager.runExchangeProcesses(), 1000L, 250L, TimeUnit.MILLISECONDS);
    }

    public static void create() {
        if (instance == null) {
            instance = new ExchangeManagerServer();
            instance.scheduleRepeatableProcesses();
        }
    }

    public static ExchangeManagerServer instance() {
        return instance;
    }

    public ExchangeProcessesManagerServer getExchangeProcessesManager() {
        return this.exchangeProcessesManager;
    }

    public ItemsBlackList getItemsBlacklist() {
        return this.itemsBlacklist;
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {       
        this.exchangeProcessesManager.onPlayerUnloaded(playerMP);
    }
}
