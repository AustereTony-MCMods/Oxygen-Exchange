package austeretony.oxygen_exchange.common.config;

import java.util.Queue;

import austeretony.oxygen.common.api.config.AbstractConfigHolder;
import austeretony.oxygen.common.api.config.ConfigValue;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen_exchange.common.main.ExchangeMain;

public class ExchangeConfig extends AbstractConfigHolder {

    public static final ConfigValue
    EXCHANGE_REQUEST_EXPIRE_TIME = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "exchange_request_expire_time_seconds");

    @Override
    public String getModId() {
        return ExchangeMain.MODID;
    }

    @Override
    public String getVersion() {
        return ExchangeMain.VERSION_EXTENDED;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/exchange/exchange.json";
    }

    @Override
    public String getInternalPath() {
        return "assets/oxygen_exchange/exchange.json";
    }

    @Override
    public void getValues(Queue<ConfigValue> values) {
        values.add(EXCHANGE_REQUEST_EXPIRE_TIME);
    }

    @Override
    public boolean sync() {
        return false;
    }
}
