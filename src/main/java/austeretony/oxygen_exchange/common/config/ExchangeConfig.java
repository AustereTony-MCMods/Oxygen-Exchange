package austeretony.oxygen_exchange.common.config;

import java.util.List;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfigHolder;
import austeretony.oxygen_core.common.api.config.ConfigValueImpl;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_exchange.common.main.ExchangeMain;

public class ExchangeConfig extends AbstractConfigHolder {

    public static final ConfigValue EXCHANGE_REQUEST_EXPIRE_TIME_SECONDS = new ConfigValueImpl(EnumValueType.INT, "main", "exchange_request_expire_time_seconds");

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
    public String getInternalPath() {
        return "assets/oxygen_exchange/exchange.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(EXCHANGE_REQUEST_EXPIRE_TIME_SECONDS);
    }

    @Override
    public boolean sync() {
        return false;
    }
}
