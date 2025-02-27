package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class BasePocket<T extends BasePeripheral> extends AbstractPocketUpgrade {

    protected T peripheral;

    protected BasePocket(ResourceLocation id, String adjective, Supplier<? extends IItemProvider> stack) {
        super(id, adjective, stack);
    }

    protected abstract T getPeripheral(IPocketAccess access);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess iPocketAccess) {
        peripheral = getPeripheral(iPocketAccess);
        return peripheral;
    }

    @Override
    public boolean onRightClick(@NotNull World world, @NotNull IPocketAccess access, @Nullable IPeripheral peripheral) {
        if (access.getEntity() != null) {
            this.peripheral.setEntity(access.getEntity());
        }
        return false;
    }
}
