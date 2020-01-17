package austeretony.oxygen_exchange.client.settings.gui;

import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.settings.SettingValue;
import austeretony.oxygen_core.common.settings.SettingValueUtils;

public enum EnumExchangeGUISetting {

    //Color

    SLOT_BOTTOM_LAYER_COLOR("color_slot_bottom_layer", EnumValueType.HEX, Integer.toHexString(0xdc252525)),
    SLOT_HIGHLIGHTING_COLOR("color_slot_highlighting", EnumValueType.HEX, Integer.toHexString(0x45ffffff)),

    //Alignment

    EXCHANGE_MENU_ALIGNMENT("alignment_exchange_menu", EnumValueType.INT, String.valueOf(0));

    private final String key, baseValue;

    private final EnumValueType type;

    private SettingValue value;

    EnumExchangeGUISetting(String key, EnumValueType type, String baseValue) {
        this.key = key;
        this.type = type;
        this.baseValue = baseValue;
    }

    public SettingValue get() {
        if (this.value == null)
            this.value = OxygenManagerClient.instance().getClientSettingManager().getSettingValue(this.key);
        return this.value;
    }

    public static void register() {
        for (EnumExchangeGUISetting setting : EnumExchangeGUISetting.values())
            OxygenManagerClient.instance().getClientSettingManager().register(SettingValueUtils.getValue(setting.type, setting.key, setting.baseValue));
    }
}
