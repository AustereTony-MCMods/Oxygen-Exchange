package austeretony.oxygen_exchange.client;

public class ExchangeManagerClient {

    private static ExchangeManagerClient instance;

    private final ExchangeOperationsManagerClient exchangeOperationsManager = new ExchangeOperationsManagerClient();

    private final ExchangeMenuManager exchangeMenuManager = new ExchangeMenuManager();

    public static void create() {
        if (instance == null)
            instance = new ExchangeManagerClient();
    }

    public static ExchangeManagerClient instance() {
        return instance;
    }

    public ExchangeOperationsManagerClient getExchangeOperationsManager() {
        return this.exchangeOperationsManager;
    }

    public ExchangeMenuManager getExchangeMenuManager() {
        return this.exchangeMenuManager;
    }
}
