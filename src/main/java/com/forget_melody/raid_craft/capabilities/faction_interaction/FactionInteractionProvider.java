package com.forget_melody.raid_craft.capabilities.faction_interaction;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FactionInteractionProvider implements ICapabilitySerializable<CompoundTag> {
	private final IFactionInteraction instance;
	private final LazyOptional<IFactionInteraction> lazyOptional;
	
	public FactionInteractionProvider(ServerPlayer player) {
		this.instance = new FactionInteraction(player);
		this.lazyOptional = LazyOptional.of(() -> this.instance);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.FACTION_INTERACTION.orEmpty(cap, lazyOptional);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return instance.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		instance.deserializeNBT(nbt);
	}
}
