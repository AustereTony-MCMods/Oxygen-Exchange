package austeretony.oxygen_exchange.client;

import austeretony.oxygen_core.common.chat.ChatMessagesHandler;
import austeretony.oxygen_exchange.common.main.EnumExchangeStatusMessage;
import austeretony.oxygen_exchange.common.main.ExchangeMain;

public class ExchangeStatusMessagesHandler implements ChatMessagesHandler {

    @Override
    public int getModIndex() {
        return ExchangeMain.EXCHANGE_MOD_INDEX;
    }

    @Override
    public String getMessage(int messageIndex) {
        return EnumExchangeStatusMessage.values()[messageIndex].localizedName();
    }
}
