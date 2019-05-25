package austeretony.oxygen_exchange.common;

import austeretony.oxygen.common.api.process.AbstractPersistentProcess;

public class RunExchangeProcess extends AbstractPersistentProcess {

    @Override
    public boolean hasDelay() {
        return false;
    }

    @Override
    public int getExecutionDelay() {
        return 0;
    }

    @Override
    public void execute() {
        ExchangeManagerServer.instance().runExchangeProcesses();
    }
}
