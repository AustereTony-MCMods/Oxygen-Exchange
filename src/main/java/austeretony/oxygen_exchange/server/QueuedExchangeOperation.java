package austeretony.oxygen_exchange.server;

import austeretony.oxygen_exchange.common.EnumExchangeOperation;

public class QueuedExchangeOperation {

    final int playerIndex;

    final EnumExchangeOperation operation;

    final long offeredCurrency;

    protected QueuedExchangeOperation(int playerIndex, EnumExchangeOperation operation, long offeredCurrency) {
        this.playerIndex = playerIndex;
        this.operation = operation;
        this.offeredCurrency = offeredCurrency;
    }
}
