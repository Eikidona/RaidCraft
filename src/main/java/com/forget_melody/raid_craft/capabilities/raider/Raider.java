package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.Raid;
import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.capabilities.raid_manager.RaidManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Raider implements IRaider, INBTSerializable<CompoundTag> {
	public static final ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "raider");
	public static final Raider EMPTY = new Raider(null);
	
	private Mob mob;
	private Raid raid;
	private int wave;
	
	public Raider(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	public Mob get() {
		return mob;
	}
	
	@Nullable
	@Override
	public Raid getRaid() {
		return raid;
	}
	
	@Override
	public void setRaid(Raid raid) {
		this.raid = raid;
	}
	
	@Override
	public void setWave(Integer wave) {
		this.wave = wave;
	}
	
	@Override
	public int getWave() {
		return wave;
	}
	
	@Override
	public boolean hasActiveRaid() {
		return raid == null ? false : raid.isStarted();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag compoundTag = new CompoundTag();
		if (raid != null) {
			compoundTag.putInt("Raid", raid.getId());
		}
		return compoundTag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("Raid")) {
			IRaidManager raidManager = mob.level().getCapability(Capabilities.RAID_MANAGER).orElse(RaidManager.EMPTY);
			if (raidManager != RaidManager.EMPTY) {
				this.raid = raidManager.getRaid(nbt.getInt("Raid"));
			}
			if (this.raid != null) {
				this.raid.addWaveMob(this.wave, mob);
			}
		}
	}
	
	public static class Provider implements ICapabilitySerializable<CompoundTag> {
		private IRaider raider;
		private LazyOptional<IRaider> raiderLazyOptional;
		
		public Provider(Mob mob) {
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
	
}
