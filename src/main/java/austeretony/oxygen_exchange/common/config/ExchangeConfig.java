package austeretony.oxygen_exchange.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_exchange.common.main.ExchangeMain;

public class ExchangeConfig extends AbstractConfig {

    public static final ConfigValue 
    ALLOW_EXCHANGE = ConfigValueUtils.getValue("server", "allow_exchange", true, true),
    EXCHANGE_REQUEST_EXPIRE_TIME_SECONDS = ConfigValueUtils.getValue("server", "exchange_request_expire_time_seconds", 20),
    ADVANCED_LOGGING = ConfigValueUtils.getValue("server", "advanced_logging", false);

    @Override
    public String getDomain() {
        return ExchangeMain.MODID;
    }

    @Override
    public String getVersion() {
        return ExchangeMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/exchange.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(ALLOW_EXCHANGE);
        values.add(EXCHANGE_REQUEST_EXPIRE_TIME_SECONDS);
        values.add(ADVANCED_LOGGING);
    }
}
