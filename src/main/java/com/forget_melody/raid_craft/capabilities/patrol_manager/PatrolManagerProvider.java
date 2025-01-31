package com.forget_melody.raid_craft.capabilities.patrol_manager;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatrolManagerProvider implements ICapabilitySerializable<CompoundTag> {
	private final IPatrolManager patrolManager;
	private final LazyOptional<IPatrolManager> lazyOptional;
	
	public PatrolManagerProvider(ServerLevel level) {
		this.patrolManager = new PatrolManager(level);
		this.lazyOptional = LazyOptional.of(() -> patrolManager);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.PATROLLER_MANAGER.orEmpty(cap, lazyOptional).cast();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return patrolManager.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		patrolManager.deserializeNBT(nbt);
	}
}
