package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaiderProvider implements ICapabilitySerializable<CompoundTag> {
	public static final ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "raider");
	private final IRaider raider;
	private final LazyOptional<IRaider> raiderLazyOptional;
	
	public RaiderProvider(Mob mob) {
		this.raider = new Raider(mob);
		this.raiderLazyOptional = LazyOptional.of(() -> raider);
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
