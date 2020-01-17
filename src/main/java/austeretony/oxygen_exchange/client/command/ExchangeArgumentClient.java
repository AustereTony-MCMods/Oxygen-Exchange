package austeretony.oxygen_exchange.client.command;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class ExchangeArgumentClient implements ArgumentExecutor {

    @Override
    public String getName() {
        return "exchange";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            if (args[1].equals("-offer")) {
                Entity entity = ClientReference.getPointedEntity();
                if (entity != null && entity instanceof EntityPlayer)
                    ExchangeManagerClient.instance().getExchangeOperationsManager().sendExchangeRequestSynced(ClientReference.getPersistentUUID(entity));
            }
        }
    }
}
