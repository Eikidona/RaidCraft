package com.forget_melody.raid_craft.capabilities.raid_interaction;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaidInteractionProvider implements ICapabilitySerializable<CompoundTag> {
	private final IRaidInteraction raidInteraction;
	private final LazyOptional<IRaidInteraction> lazyOptional;
	
	public RaidInteractionProvider(ServerPlayer player) {
		this.raidInteraction = new RaidInteraction(player);
		this.lazyOptional = LazyOptional.of(() -> this.raidInteraction);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.RAID_INTERACTION.orEmpty(cap, lazyOptional);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return raidInteraction.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		raidInteraction.deserializeNBT(nbt);
	}
}
