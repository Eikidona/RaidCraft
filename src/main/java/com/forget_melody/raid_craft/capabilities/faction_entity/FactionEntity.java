package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.faction.Faction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public class FactionEntity implements IFactionEntity{
	private final Mob mob;
	private Faction faction;
	
	
	public FactionEntity(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	@Nullable
	public Faction getFaction() {
		return faction;
	}
	
	@Override
	public Mob getEntity() {
		return mob;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
	
	}
}
