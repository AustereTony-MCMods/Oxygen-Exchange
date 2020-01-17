package austeretony.oxygen_exchange.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PlayerInteractionMenuHelper;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.gui.settings.SettingsScreen;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.network.NetworkRequestsRegistryServer;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import austeretony.oxygen_exchange.client.ExchangeStatusMessagesHandler;
import austeretony.oxygen_exchange.client.command.ExchangeArgumentClient;
import austeretony.oxygen_exchange.client.event.ExchangeEventsClient;
import austeretony.oxygen_exchange.client.gui.settings.ExchangeSettingsContainer;
import austeretony.oxygen_exchange.client.interaction.OfferExchangeExecutor;
import austeretony.oxygen_exchange.client.settings.gui.EnumExchangeGUISetting;
import austeretony.oxygen_exchange.common.config.ExchangeConfig;
import austeretony.oxygen_exchange.common.network.client.CPExchangeOperation;
import austeretony.oxygen_exchange.common.network.client.CPSyncOtherPlayerOffer;
import austeretony.oxygen_exchange.common.network.server.SPExchangeMenuOperation;
import austeretony.oxygen_exchange.common.network.server.SPSendExchangeRequest;
import austeretony.oxygen_exchange.server.ExchangeManagerServer;
import austeretony.oxygen_exchange.server.event.ExchangeEventsServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = ExchangeMain.MODID, 
        name = ExchangeMain.NAME, 
        version = ExchangeMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.10.0,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = ExchangeMain.VERSIONS_FORGE_URL)
public class ExchangeMain {

    public static final String 
    MODID = "oxygen_exchange", 
    NAME = "Oxygen: Exchange", 
    VERSION = "0.10.0", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Exchange/info/mod_versions_forge.json";

    public static final int 
    EXCHANGE_MOD_INDEX = 3,

    EXCHANGE_REQUEST_ID = 30,

    EXCHANGE_MENU_SCREEN_ID = 30,

    EXCHANGE_OPERATION_REQUEST_ID = 30;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Instance(MODID)
    public static ExchangeMain instance; 

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new ExchangeConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgument(new ExchangeArgumentClient());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        ExchangeManagerServer.create();
        CommonReference.registerEvent(new ExchangeEventsServer());
        NetworkRequestsRegistryServer.registerRequest(EXCHANGE_OPERATION_REQUEST_ID, 1000);
        EnumExchangePrivilege.register();
        if (event.getSide() == Side.CLIENT) {  
            ExchangeManagerClient.create();
            CommonReference.registerEvent(new ExchangeEventsClient());
            PlayerInteractionMenuHelper.registerInteractionMenuEntry(new OfferExchangeExecutor());
            OxygenHelperClient.registerStatusMessagesHandler(new ExchangeStatusMessagesHandler());
            EnumExchangeGUISetting.register();
            SettingsScreen.registerSettingsContainer(new ExchangeSettingsContainer());
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ExchangeGUIHandler());
    }   

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPExchangeOperation.class);
        OxygenMain.network().registerPacket(CPSyncOtherPlayerOffer.class);

        OxygenMain.network().registerPacket(SPSendExchangeRequest.class);
        OxygenMain.network().registerPacket(SPExchangeMenuOperation.class);
    }
}
