package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.IFaction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.world.entity.ai.goal.faction_entity.NearestEnemyFactionEntityTargetGoal;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import com.forget_melody.raid_craft.registries.datapack.api.Factions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public class FactionEntity implements IFactionEntity {
	private final Mob mob;
	private IFaction faction = Factions.GAIA;
	private FactionEntityType factionEntityType;
	private NearestEnemyFactionEntityTargetGoal goal;
	
	public FactionEntity(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	@Nullable
	public IFaction getFaction() {
		return faction;
	}
	
	@Override
	public Mob getEntity() {
		return mob;
	}
	
	@Override
	public boolean isEnemy(IFaction faction) {
		return faction.getFactionRelations().getEnemies().add(DatapackRegistries.FACTIONS.getKey((Faction) faction));
	}
	
	@Override
	public boolean isAlly(IFaction faction) {
		return faction.getFactionRelations().getAllies().add(DatapackRegistries.FACTIONS.getKey((Faction) faction));
	}
	
	@Override
	public boolean isFriendly(Mob mob) {
		return IFactionEntity.getFactionEntity(mob).get().isAlly(faction);
	}
	
	@Override
	public boolean isHostility(Mob mob) {
		return IFactionEntity.getFactionEntity(mob).get().isEnemy(faction);
	}
	
	@Override
	public void setFaction(IFaction IFaction) {
		this.faction = IFaction;
		updateGoal();
	}
	
	private void updateGoal(){
		if(faction != Factions.GAIA && goal == null){
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
		IFaction IFaction = DatapackRegistries.FACTIONS.getValue(new ResourceLocation(nbt.getString("Faction")));
		setFaction(IFaction);
	}
}
