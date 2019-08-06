package austeretony.oxygen_exchange.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.interaction.InteractionHelperClient;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.event.ExchangeEventsClient;
import austeretony.oxygen_exchange.client.gui.OfferExchangeExecutor;
import austeretony.oxygen_exchange.common.ExchangeManagerServer;
import austeretony.oxygen_exchange.common.RunExchangeProcesses;
import austeretony.oxygen_exchange.common.command.CommandOfferExchange;
import austeretony.oxygen_exchange.common.config.ExchangeConfig;
import austeretony.oxygen_exchange.common.event.ExchangeEventsServer;
import austeretony.oxygen_exchange.common.network.client.CPExchangeCommand;
import austeretony.oxygen_exchange.common.network.client.CPSyncOtherPlayerOffer;
import austeretony.oxygen_exchange.common.network.server.SPExchangeMenuOperation;
import austeretony.oxygen_exchange.common.network.server.SPSendExchangeRequest;
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
        dependencies = "required-after:oxygen@[0.8.0,);",//TODO Always check required Oxygen version before build
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = ExchangeMain.VERSIONS_FORGE_URL)
public class ExchangeMain {

    public static final String 
    MODID = "oxygen_exchange", 
    NAME = "Oxygen: Exchange", 
    VERSION = "0.8.0", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Exchange/info/mod_versions_forge.json";

    public static final int 
    EXCHANGE_MOD_INDEX = 3,//Oxygen - 0, Teleportation - 1, Groups - 2, Merchants - 4, Players List - 5, Friends List - 6, Interaction - 7, Mail - 8, Chat - 9

    EXCHANGE_REQUEST_ID = 30,

    EXCHANGE_MENU_SCREEN_ID = 30;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static OxygenNetwork network;

    @Instance(MODID)
    public static ExchangeMain instance; 

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperServer.registerConfig(new ExchangeConfig());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        ExchangeManagerServer.create();
        CommonReference.registerEvent(new ExchangeEventsServer());
        OxygenHelperServer.addPersistentServiceProcess(new RunExchangeProcesses());
        if (event.getSide() == Side.CLIENT) {  
            ExchangeManagerClient.create();
            CommonReference.registerEvent(new ExchangeEventsClient());
            InteractionHelperClient.registerInteractionMenuAction(new OfferExchangeExecutor());
            OxygenHelperClient.registerNotificationIcon(EXCHANGE_REQUEST_ID, OxygenGUITextures.REQUEST_ICONS);
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ExchangeGUIHandler());
    }   

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        CommonReference.registerCommand(event, new CommandOfferExchange());
    }

    private void initNetwork() {
        network = OxygenHelperServer.createNetworkHandler(MODID);

        network.registerPacket(CPExchangeCommand.class);
        network.registerPacket(CPSyncOtherPlayerOffer.class);

        network.registerPacket(SPSendExchangeRequest.class);
        network.registerPacket(SPExchangeMenuOperation.class);
    }

    public static OxygenNetwork network() {
        return network;
    }
}
