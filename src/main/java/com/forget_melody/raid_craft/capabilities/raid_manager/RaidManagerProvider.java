package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaidManagerProvider implements ICapabilitySerializable<CompoundTag> {
	private IRaidManager raidManager;
	private LazyOptional<IRaidManager> raidManagerLazyOptional;
	
	public RaidManagerProvider(ServerLevel level) {
		raidManager = new RaidManager(level);
		this.raidManagerLazyOptional = LazyOptional.of(() -> raidManager);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.RAID_MANAGER.orEmpty(cap, raidManagerLazyOptional);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return raidManager.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		raidManager.deserializeNBT(nbt);
	}
}