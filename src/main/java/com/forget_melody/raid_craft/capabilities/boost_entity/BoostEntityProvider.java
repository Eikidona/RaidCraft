package com.forget_melody.raid_craft.capabilities.boost_entity;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoostEntityProvider implements ICapabilitySerializable<CompoundTag> {
	private final IBoostEntity instance;
	private final LazyOptional<IBoostEntity> lazyOptional;
	
	public BoostEntityProvider(Mob mob) {
		this.instance = new BoostEntity(mob);
		this.lazyOptional = LazyOptional.of(() ->this.instance);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.BOOST_ENTITY.orEmpty(cap, lazyOptional);
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
