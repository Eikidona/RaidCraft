package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.InvadeHomeGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.RaidOpenDoorGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class VillageRaid extends Raid {
	public VillageRaid(int id, ServerLevel level, Faction faction, BlockPos center) {
		super(id, level, faction, center);
	}
	
	public VillageRaid(ServerLevel level, CompoundTag tag) {
		super(level, tag);
	}
	
	@Override
	protected RaidType getType() {
		return RaidType.VILLAGE;
	}
	
	@Override
	protected boolean checkLoseCondition() {
		return level.getPoiManager().getInRange(holder -> holder.is(PoiTypeTags.VILLAGE), center, 0, PoiManager.Occupancy.IS_OCCUPIED).findFirst().isEmpty();
	}
	
	@Override
	protected void updateTargetPos() {
		level.getPoiManager().getInRange(holder -> holder.is(PoiTypeTags.VILLAGE), center, 48, PoiManager.Occupancy.IS_OCCUPIED).findFirst().ifPresent(poiRecord -> setCenter(poiRecord.getPos()));
	}
	
	@Override
	protected int computedStrength() {
		List<IFactionEntity> factionEntities = level.getEntitiesOfClass(Mob.class, new AABB(center.offset(-56, -4, -56), center.offset(56, 4, 56)), this::isEnemyMobOfRaider).stream().map(IFactionEntity::get).toList();
		int strength = 0;
		for (IFactionEntity factionEntity : factionEntities) {
			boolean isEnemy = false;
			for (Faction faction : factions) {
				if (faction.isEnemy(factionEntity.getFaction())) {
					isEnemy = true;
					break;
				}
			}
			
			if (isEnemy) {
				strength += factionEntity.getStrength();
			}
		}
		return strength;
	}
	
	@Override
	protected void addGoals(IRaider raider) {
		super.addGoals(raider);
		raider.addGoal(3, new RaidOpenDoorGoal(raider.getMob()));
		raider.addGoal(4, new InvadeHomeGoal(raider.getMob(), 1.05F, 1));
	}
}
