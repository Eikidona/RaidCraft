package com.forget_melody.raid_craft.raid.raid.target;

import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.raid.Raid;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.InvadeHomeGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.ObtainRaidLeaderBannerGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.RaidOpenDoorGoal;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class VillageRaidTarget implements IRaidTarget {
	
	@Override
	public void updateTargetPos(Raid raid) {
		Optional<PoiRecord> poiRecord = raid.getLevel().getPoiManager().getInRange(holder -> holder.is(PoiTypeTags.VILLAGE), raid.getCenter(), 48, PoiManager.Occupancy.IS_OCCUPIED).findFirst();
		poiRecord.ifPresent(poiRecord1 -> raid.setCenter(poiRecord1.getPos()));
	}
	
	@Override
	public boolean checkLoseCondition(Raid raid) {
		return raid.getLevel().getPoiManager().getInRange(holder -> holder.is(PoiTypeTags.VILLAGE), raid.getCenter(), 0, PoiManager.Occupancy.IS_OCCUPIED).findFirst().isEmpty();
	}
	
	@Override
	public int getTargetStrength(Raid raid) {
		List<IFactionEntity> factionEntities = raid.getLevel().getEntitiesOfClass(Mob.class, new AABB(raid.getCenter().offset(-56, -4, -56), raid.getCenter().offset(56, 4, 56)), raid::isTargetMob).stream().map(IFactionEntity::get).toList();
		int strength = 0;
//		RaidCraft.LOGGER.info("factionEntities Size: {}", factionEntities.size());
		for (IFactionEntity factionEntity : factionEntities) {
			boolean isEnemy = false;
			for (Faction faction : raid.getFactions()) {
				if (faction.isEnemy(factionEntity.getFaction())) {
					isEnemy = true;
					break;
				}
			}
			
			if (isEnemy) {
				strength += factionEntity.getStrength();
//				if (factionEntity.getFactionEntityType() != null) {
//					strength += factionEntity.getFactionEntityType().getStrength();
//				} else {
//					strength += 5;
//				}
			}
		}
		return strength;
	}
	
	@Override
	public void addGoal(IRaider raider) {
		IRaidTarget.super.addGoal(raider);
		raider.addGoal(2, new ObtainRaidLeaderBannerGoal(raider.getMob()));
		if (GoalUtils.hasGroundPathNavigation(raider.getMob())) {
			raider.addGoal(3, new RaidOpenDoorGoal(raider.getMob()));
		}
		raider.addGoal(4, new InvadeHomeGoal(raider.getMob(), 1.05F, 1));
	}
}
