package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatrollerProvider implements ICapabilitySerializable<CompoundTag> {
	private final IPatroller patroller;
	private final LazyOptional<IPatroller> optional;
	
	public PatrollerProvider(Mob mob) {
		this.patroller = new Patroller(mob);
		this.optional = LazyOptional.of(() -> this.patroller);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.PATROLLER.orEmpty(cap, optional);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return patroller.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		patroller.deserializeNBT(nbt);
	}
}
