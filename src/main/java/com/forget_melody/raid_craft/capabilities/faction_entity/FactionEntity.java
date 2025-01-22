package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.IFaction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import com.forget_melody.raid_craft.registries.datapack.api.Factions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class FactionEntity implements IFactionEntity {
	private final Mob mob;
	private IFaction IFaction;
	private FactionEntityType factionEntityType;
	
	public FactionEntity(Mob mob) {
		this.mob = mob;
		IFaction faction = DatapackRegistries.FACTIONS.getReMapValue(mob.getType());
		
//		for (IFaction IFaction : DatapackRegistries.FACTIONS.getValues()) {
//			if(IFaction.getEntities().contains(ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()))){
//				setFaction(IFaction);
//				break;
//			}
//		}
		
		if(IFaction != null){
			setFaction(faction);
		}else {
			setFaction(Factions.GAIA);
		}
	}
	
	@Override
	@Nullable
	public IFaction getFaction() {
		return IFaction;
	}
	
	@Override
	public Mob getEntity() {
		return mob;
	}
	
	@Override
	public void setFaction(IFaction IFaction) {
		this.IFaction = IFaction;
	}
	
	@Override
	public void setFactionEntityType(FactionEntityType factionEntityType) {
		this.factionEntityType = factionEntityType;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if(this.IFaction != null){
			tag.putString("Faction", DatapackRegistries.FACTIONS.getKey((Faction) this.IFaction).toString());
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
