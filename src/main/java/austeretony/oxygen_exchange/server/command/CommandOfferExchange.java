package austeretony.oxygen_exchange.server.command;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_exchange.server.ExchangeManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandOfferExchange extends CommandBase {

    @Override
    public String getName() {
        return "exchange";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/exchange <username>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1)
            throw new WrongUsageException(this.getUsage(sender));   
        EntityPlayerMP 
        senderMP,
        requestedMP = CommonReference.playerByUsername(args[0]);
        if (requestedMP != null) {
            senderMP = getCommandSenderAsPlayer(sender);
            ExchangeManagerServer.instance().getExchangeProcessesManager().sendExchangeRequest(senderMP, CommonReference.getEntityId(requestedMP));
        } else
            throw new CommandException("oxygen.command.exception.playerNotFound", args[0]);
    }
}
