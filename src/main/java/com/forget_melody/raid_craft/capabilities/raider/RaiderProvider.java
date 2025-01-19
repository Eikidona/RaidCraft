package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaiderProvider implements ICapabilitySerializable<CompoundTag> {
	private IRaider raider;
	private LazyOptional<IRaider> raiderLazyOptional;
	
	public RaiderProvider(Mob mob) {
		raider = new Raider(mob);
		raiderLazyOptional = LazyOptional.of(() -> raider);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.RAIDER.orEmpty(cap, raiderLazyOptional);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return raider.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		raider.deserializeNBT(nbt);
	}
}
