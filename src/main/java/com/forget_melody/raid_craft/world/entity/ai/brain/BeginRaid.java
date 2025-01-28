package com.forget_melody.raid_craft.world.entity.ai.brain;

import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.raid.raid.Raid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Map;

public class BeginRaid extends Behavior<Mob> {
	public BeginRaid(Map<MemoryModuleType<?>, MemoryStatus> pEntryCondition) {
		super(pEntryCondition);
	}
	
	@Override
	protected boolean checkExtraStartConditions(ServerLevel pLevel, Mob pOwner) {
		return pLevel.random.nextInt(20) == 0;
	}
	
//	@Override
//	protected void start(ServerLevel pLevel, Mob pEntity, long pGameTime) {
//		Brain<?> brain = pEntity.getBrain();
//		IRaidManager manager = IRaidManager.get(pLevel).get();
//		Raid raid = manager.getRaidAtPos(pEntity.blockPosition());
//		if (raid != null) {
//			if (raid.hasFirstWaveSpawned() && !raid.isBetweenWaves()) {
//				brain.setDefaultActivity(ModActivities.FACTION_RAID.get());
//				brain.setActiveActivityIfPossible(ModActivities.FACTION_RAID.get());
//			} else {
//				brain.setDefaultActivity(ModActivities.PRE_FACTION_RAID.get());
//				brain.setActiveActivityIfPossible(ModActivities.PRE_FACTION_RAID.get());
//			}
//		}
//
//	}
}
