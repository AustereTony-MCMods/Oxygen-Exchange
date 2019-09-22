package austeretony.oxygen_exchange.common.main;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumExchangeStatusMessage {

    EXCHANGE_REQUEST_ACCEPTED_SENDER("acceptedSender"),
    EXCHANGE_REQUEST_ACCEPTED_TARGET("acceptedTarget"),
    EXCHANGE_REQUEST_REJECTED_SENDER("rejectedSender"),
    EXCHANGE_REQUEST_REJECTED_TARGET("rejectedTarget"),
    ITEM_BLACKLISTED("itemBlacklisted");

    private final String status;

    EnumExchangeStatusMessage(String status) {
        this.status = "oxygen_exchange.status." + status;
    }

    public String localizedName() {
        return ClientReference.localize(this.status);
    }
}
