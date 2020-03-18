package austeretony.oxygen_exchange.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.item.ItemsBlackList;
import net.minecraft.entity.player.EntityPlayerMP;

public class ExchangeManagerServer {

    private static ExchangeManagerServer instance;

    private final ExchangeProcessesManagerServer exchangeProcessesManager = new ExchangeProcessesManagerServer();

    private final ItemsBlackList itemsBlacklist = ItemsBlackList.create("exchange");

    private void scheduleRepeatableProcesses() {
        OxygenHelperServer.getSchedulerExecutorService().scheduleAtFixedRate(this.exchangeProcessesManager::process, 1L, 1L, TimeUnit.SECONDS);
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

    public void playerUnloaded(EntityPlayerMP playerMP) {       
        this.exchangeProcessesManager.onPlayerUnloaded(playerMP);
    }
}
