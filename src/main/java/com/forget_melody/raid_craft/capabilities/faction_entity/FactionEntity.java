package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.forget_melody.raid_craft.world.entity.ai.goal.faction_entity.NearestEnemyFactionEntityTargetGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public class FactionEntity implements IFactionEntity {
	private final Mob mob;
	private Faction faction = Faction.DEFAULT;
	private FactionEntityType factionEntityType;
	private NearestEnemyFactionEntityTargetGoal goal;
	
	public FactionEntity(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	public Faction getFaction() {
		return faction;
	}
	
	@Override
	public FactionEntityType getFactionEntityType() {
		return factionEntityType;
	}
	
	@Override
	public Mob getMob() {
		return mob;
	}
	
	@Override
	public boolean isEnemy(Faction targetFaction) {
		if(targetFaction == null){
			RaidCraft.LOGGER.error("isEnemy NullPointerException by entity type id {}", ForgeRegistries.ENTITY_TYPES.getKey(this.mob.getType()));
			return false;
		}
		return targetFaction != Faction.DEFAULT && faction != Faction.DEFAULT && faction.isEnemy(targetFaction);
	}
	
	@Override
	public boolean isAlly(Faction targetFaction) {
		if(targetFaction == null){
			RaidCraft.LOGGER.error("isAlly NullPointerException by entity type id {}", ForgeRegistries.ENTITY_TYPES.getKey(this.mob.getType()));
			return false;
		}
		return targetFaction != Faction.DEFAULT && faction != Faction.DEFAULT && faction.isAlly(targetFaction);
	}
	
	@Override
	public boolean isFriendly(Mob target) {
		return IFactionEntity.get(target).isAlly(faction);
	}
	
	@Override
	public boolean isHostility(Mob target) {
		return IFactionEntity.get(target).isEnemy(faction);
	}
	
	@Override
	public void setFaction(Faction faction) {
		this.faction = faction;
		updateGoal();
	}
	
	private void updateGoal(){
		if(faction != Faction.DEFAULT && goal == null){
			goal = new NearestEnemyFactionEntityTargetGoal(mob);
			mob.goalSelector.addGoal(2, goal);
		}
	}
	
	@Override
	public void setFactionEntityType(FactionEntityType factionEntityType) {
		this.factionEntityType = factionEntityType;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if(this.faction != null){
			tag.putString("Faction", DataPackRegistries.FACTIONS.getKey((Faction) this.faction).toString());
		}
		if(this.factionEntityType != null){
			tag.putString("FactionEntityType", DataPackRegistries.Faction_ENTITY_TYPES.getKey(this.factionEntityType).toString());
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		Faction faction = DataPackRegistries.FACTIONS.getValue(new ResourceLocation(nbt.getString("Faction")));
		setFaction(faction);
	}
}
