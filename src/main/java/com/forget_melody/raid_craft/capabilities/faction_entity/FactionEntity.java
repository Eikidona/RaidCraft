package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.world.entity.ai.goal.faction_entity.NearestEnemyFactionEntityTargetGoal;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import com.forget_melody.raid_craft.registries.Factions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public class FactionEntity implements IFactionEntity {
	private final Mob mob;
	private Faction faction = Factions.DEFAULT;
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
	public Mob getMob() {
		return mob;
	}
	
	@Override
	public boolean isEnemy(Faction faction) {
		if(faction == null){
			RaidCraft.LOGGER.error("isEnemy NullPointerException by entity type id {}", ForgeRegistries.ENTITY_TYPES.getKey(this.mob.getType()));
			return false;
		}
		return faction.getFactionRelations().getEnemies().add(DatapackRegistries.FACTIONS.getKey(faction));
	}
	
	@Override
	public boolean isAlly(Faction faction) {
		if(faction == null){
			RaidCraft.LOGGER.error("isAlly NullPointerException by entity type id {}", ForgeRegistries.ENTITY_TYPES.getKey(this.mob.getType()));
			return false;
		}
		return faction.getFactionRelations().getAllies().add(DatapackRegistries.FACTIONS.getKey(faction));
	}
	
	@Override
	public boolean isFriendly(Mob mob) {
		return IFactionEntity.get(mob).get().isAlly(faction);
	}
	
	@Override
	public boolean isHostility(Mob mob) {
		return IFactionEntity.get(mob).get().isEnemy(faction);
	}
	
	@Override
	public void setFaction(Faction faction) {
		this.faction = faction;
		updateGoal();
	}
	
	private void updateGoal(){
		if(faction != Factions.DEFAULT && goal == null){
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
			tag.putString("Faction", DatapackRegistries.FACTIONS.getKey((Faction) this.faction).toString());
		}
		if(this.factionEntityType != null){
//			tag.putString("FactionEntityType", DatapackRegistries..getKey(this.faction).toString());
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		Faction IFaction = DatapackRegistries.FACTIONS.getValue(new ResourceLocation(nbt.getString("Faction")));
		setFaction(IFaction);
	}
}
