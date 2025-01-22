package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.IFaction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

@AutoRegisterCapability
public interface IFactionEntity extends INBTSerializable<CompoundTag> {
	static Optional<IFactionEntity> getFactionEntity(Mob mob) {
		return mob.getCapability(Capabilities.FACTION_ENTITY).resolve();
	}
	
	IFaction getFaction();
	
	Mob getEntity();
	
	void setFaction(IFaction IFaction);
	
	void setFactionEntityType(FactionEntityType factionEntityType);
}
