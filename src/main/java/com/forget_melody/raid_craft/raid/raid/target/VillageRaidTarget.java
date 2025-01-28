package com.forget_melody.raid_craft.raid.raid.target;

import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.raid.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class VillageRaidTarget implements IRaidTarget{
	@Override
	public Optional<BlockPos> updateTargetPos(Raid raid) {
		Optional<PoiRecord> poiRecord = raid.getLevel().getPoiManager().getInRange(holder -> holder.is(PoiTypeTags.VILLAGE), raid.getCenter(), 32, PoiManager.Occupancy.IS_OCCUPIED).findFirst();
		return poiRecord.map(PoiRecord::getPos);
	}
	
	@Override
	public boolean isValidTarget(Raid raid) {
		return raid.getLevel().getPoiManager().getInRange(holder -> holder.is(PoiTypeTags.VILLAGE), raid.getCenter(), 32, PoiManager.Occupancy.IS_OCCUPIED).findFirst().isPresent();
	}
	
	@Override
	public int getTargetStrength(Raid raid) {
		List<IFactionEntity> list = raid.getLevel().getEntitiesOfClass(Mob.class, new AABB(raid.getCenter().offset(-16, -16, -16), raid.getCenter().offset(16, 16, 16)), raid::isTarget).stream().map(mob -> IFactionEntity.get(mob).get()).toList();
		int strength = 0;
		for(IFactionEntity factionEntity: list){
			if(factionEntity.getFactionEntityType() != null){
				strength+=factionEntity.getFactionEntityType().getStrength();
			}else {
				strength += 5;
			}
		}
		return strength;
	}
}
