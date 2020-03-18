package austeretony.oxygen_exchange.client.command;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_exchange.client.ExchangeManagerClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
                    ExchangeManagerClient.instance().getExchangeOperationsManager().sendExchangeRequestSynced(ClientReference.getEntityId(entity));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "-offer");
        return Collections.<String>emptyList();
    }
}
