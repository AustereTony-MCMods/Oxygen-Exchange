package austeretony.oxygen_exchange.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.interaction.InteractionHelperClient;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.ExchangeStatusMessagesHandler;
import austeretony.oxygen_exchange.client.event.ExchangeEventsClient;
import austeretony.oxygen_exchange.client.gui.exchange.OfferExchangeExecutor;
import austeretony.oxygen_exchange.common.config.ExchangeConfig;
import austeretony.oxygen_exchange.common.network.client.CPExchangeOperation;
import austeretony.oxygen_exchange.common.network.client.CPSyncOtherPlayerOffer;
import austeretony.oxygen_exchange.common.network.server.SPExchangeMenuOperation;
import austeretony.oxygen_exchange.common.network.server.SPSendExchangeRequest;
import austeretony.oxygen_exchange.server.ExchangeManagerServer;
import austeretony.oxygen_exchange.server.command.CommandOfferExchange;
import austeretony.oxygen_exchange.server.event.ExchangeEventsServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = ExchangeMain.MODID, 
        name = ExchangeMain.NAME, 
        version = ExchangeMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.9.1,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = ExchangeMain.VERSIONS_FORGE_URL)
public class ExchangeMain {

    public static final String 
    MODID = "oxygen_exchange", 
    NAME = "Oxygen: Exchange", 
    VERSION = "0.9.0", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Exchange/info/mod_versions_forge.json";

    public static final int 
    EXCHANGE_MOD_INDEX = 3,

    EXCHANGE_REQUEST_ID = 30,

    EXCHANGE_MENU_SCREEN_ID = 30,

    EXCHANGE_REQUEST_REQUEST_ID = 30,
    EXCHANGE_OPERATION_REQUEST_ID = 31;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Instance(MODID)
    public static ExchangeMain instance; 

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new ExchangeConfig());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        ExchangeManagerServer.create();
        CommonReference.registerEvent(new ExchangeEventsServer());
        RequestsFilterHelper.registerNetworkRequest(EXCHANGE_REQUEST_REQUEST_ID, 1);
        RequestsFilterHelper.registerNetworkRequest(EXCHANGE_OPERATION_REQUEST_ID, 1);
        if (event.getSide() == Side.CLIENT) {  
            ExchangeManagerClient.create();
            CommonReference.registerEvent(new ExchangeEventsClient());
            InteractionHelperClient.registerInteractionMenuEntry(new OfferExchangeExecutor());
            OxygenHelperClient.registerStatusMessagesHandler(new ExchangeStatusMessagesHandler());
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ExchangeGUIHandler());
    }   

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        CommonReference.registerCommand(event, new CommandOfferExchange());
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPExchangeOperation.class);
        OxygenMain.network().registerPacket(CPSyncOtherPlayerOffer.class);

        OxygenMain.network().registerPacket(SPSendExchangeRequest.class);
        OxygenMain.network().registerPacket(SPExchangeMenuOperation.class);
    }
}
