package austeretony.oxygen_exchange.common.main;

import austeretony.oxygen_exchange.client.gui.exchange.ExchangeMenuContainer;
import austeretony.oxygen_exchange.common.inventory.ContainerExchangeMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ExchangeGUIHandler implements IGuiHandler {

    public static final int EXCHANGE_MENU = 0;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch(id) {
        case EXCHANGE_MENU:                                 
            return new ContainerExchangeMenu(player, player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch(id) {
        case EXCHANGE_MENU:                                 
            return new ExchangeMenuContainer(new ContainerExchangeMenu(player, player.inventory));
        }
        return null;
    }
}
