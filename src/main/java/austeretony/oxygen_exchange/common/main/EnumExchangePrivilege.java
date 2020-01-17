package austeretony.oxygen_exchange.common.main;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.privilege.PrivilegeRegistry;

public enum EnumExchangePrivilege {

    ALLOW_EXCHANGE("exchange:allowExchange", 300, EnumValueType.BOOLEAN);

    private final String name;

    private final int id;

    private final EnumValueType type;

    EnumExchangePrivilege(String name, int id, EnumValueType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public int id() {
        return id;
    }

    public static void register() {
        for (EnumExchangePrivilege privilege : EnumExchangePrivilege.values())
            PrivilegeRegistry.registerPrivilege(privilege.name, privilege.id, privilege.type);
    }
}
