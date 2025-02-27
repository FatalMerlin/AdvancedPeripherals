package de.srendi.advancedperipherals.common.events;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBox;
import de.srendi.advancedperipherals.common.blocks.base.TileEntityList;
import de.srendi.advancedperipherals.common.blocks.tileentity.ChatBoxTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import de.srendi.advancedperipherals.network.MNetwork;
import de.srendi.advancedperipherals.network.messages.ClearHudCanvasMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    @SubscribeEvent
    public static void onChatBox(ServerChatEvent event) {
        ServerWorld world = event.getPlayer().getLevel();
        TileEntityList.get(world).getTileEntities().forEach(tileEntity -> { //Events for computers
            if (tileEntity instanceof ChatBoxTile) {
                if (AdvancedPeripheralsConfig.enableChatBox) {
                    if (event.getMessage().startsWith("$")) {
                        event.setCanceled(true);
                        ChatBoxTile entity = (ChatBoxTile) tileEntity;
                        for (IComputerAccess computer : entity.getConnectedComputers()) {
                            computer.queueEvent("chat", event.getUsername(), event.getMessage().replace("$", ""));
                        }
                        return;
                    }
                    ChatBoxTile entity = (ChatBoxTile) tileEntity;
                    for (IComputerAccess computer : entity.getConnectedComputers()) {
                        computer.queueEvent("chat", event.getUsername(), event.getMessage());
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onChatTurtle(ServerChatEvent event) {
        ServerWorld world = event.getPlayer().getLevel();
        TileEntityList.get(world).getTileEntities().forEach(tileEntity -> {
            if (tileEntity == null)
                return;
            if (tileEntity instanceof TileTurtle) { //Events for turtles
                TileTurtle tileTurtle = (TileTurtle) tileEntity;
                if (tileTurtle.getUpgrade(TurtleSide.RIGHT) instanceof TurtleChatBox || tileTurtle.getUpgrade(TurtleSide.LEFT) instanceof TurtleChatBox) {
                    ServerComputer computer = tileTurtle.getServerComputer();

                    if (computer == null)
                        return;

                    if (event.getMessage().startsWith("$")) {
                        event.setCanceled(true);
                        computer.queueEvent("chat", new Object[]{event.getUsername(), event.getMessage().replace("$", "")});
                        return;
                    }
                    computer.queueEvent("chat", new Object[]{event.getUsername(), event.getMessage()});
                }
            }
        });
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        if (!(livingEntity instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
        if (event.getFrom().getItem() instanceof ARGogglesItem) {
            MNetwork.sendTo(new ClearHudCanvasMessage(), player);
        }
    }
}
