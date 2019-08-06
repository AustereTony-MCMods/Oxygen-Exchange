package austeretony.oxygen_exchange.common.main;

import austeretony.oxygen.client.core.api.ClientReference;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumExchangeChatMessage {

    EXCHANGE_REQUEST_ACCEPTED_SENDER,
    EXCHANGE_REQUEST_ACCEPTED_TARGET,
    EXCHANGE_REQUEST_REJECTED_SENDER,
    EXCHANGE_REQUEST_REJECTED_TARGET;

    public void show(String... args) {
        switch (this) {
        case EXCHANGE_REQUEST_ACCEPTED_SENDER:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_exchange.message.request.acceptedSender"));
            break;
        case EXCHANGE_REQUEST_ACCEPTED_TARGET:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_exchange.message.request.acceptedTarget"));
            break;
        case EXCHANGE_REQUEST_REJECTED_SENDER:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_exchange.message.request.rejectedSender"));
            break;
        case EXCHANGE_REQUEST_REJECTED_TARGET:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_exchange.message.request.rejectedTarget"));
            break;
        }
    }
}
