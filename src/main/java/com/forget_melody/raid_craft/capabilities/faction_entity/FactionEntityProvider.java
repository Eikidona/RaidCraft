package com.forget_melody.raid_craft.capabilities.faction_entity;

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

public class FactionEntityProvider implements ICapabilitySerializable<CompoundTag> {
	public static final ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "faction_entity");
	private final IFactionEntity factionEntity;
	private final LazyOptional<IFactionEntity> lazyOptional;
	
	public FactionEntityProvider(Mob mob) {
		this.factionEntity = new FactionEntity(mob);
		this.lazyOptional = LazyOptional.of(() -> factionEntity);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return Capabilities.FACTION_ENTITY.orEmpty(cap, lazyOptional);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return factionEntity.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		factionEntity.deserializeNBT(nbt);
	}
}
