package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaidManagerProvider implements ICapabilitySerializable<CompoundTag> {
	public static ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "raid_manager");
	private final IRaidManager raidManager;
	private final LazyOptional<IRaidManager> lazyOptional;
	
	public RaidManagerProvider(ServerLevel level) {
		this.raidManager = new RaidManager(level);
		this.lazyOptional = LazyOptional.of(() -> raidManager);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.RAID_MANAGER.orEmpty(cap, lazyOptional);
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
