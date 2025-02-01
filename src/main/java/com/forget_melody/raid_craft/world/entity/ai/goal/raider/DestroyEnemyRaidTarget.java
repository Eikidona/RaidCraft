package com.forget_melody.raid_craft.world.entity.ai.goal.raider;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.List;

public class DestroyEnemyRaidTarget extends TargetGoal {
	private final int randomInterval;
	private final List<Mob> targetMobs;
	private final List<ServerPlayer> targetPlayers;
	
	public DestroyEnemyRaidTarget(Mob pMob, List<Mob> targetMobs, List<ServerPlayer> targetPlayers) {
		super(pMob, false, false);
		randomInterval = reducedTickDelay(10);
		this.targetMobs = targetMobs;
		this.targetPlayers = targetPlayers;
	}
	
	@Override
	public boolean canUse() {
		if(!IRaider.get(mob).hasActiveRaid()){
			return false;
		}
		if(mob.getRandom().nextInt(randomInterval) == 0){
			return false;
		}
		findTarget();
		if(this.targetMob == null){
			return false;
		}
		return true;
	}
	
	private void findTarget(){
		if(mob.getRandom().nextBoolean() && !targetPlayers.isEmpty()){
//			ServerPlayer player = targetPlayers.get(mob.getRandom().nextInt(targetPlayers.size()));
			LivingEntity livingEntity = mob.level().getNearestEntity(targetPlayers, TargetingConditions.DEFAULT.copy().range(mob.getAttributeValue(Attributes.FOLLOW_RANGE)), mob, mob.getX(), mob.getY(), mob.getZ());
			setTarget(livingEntity);
		}else if(!targetMobs.isEmpty()){
//			Mob mob1 = targetMobs.get(mob.getRandom().nextInt(targetMobs.size()));
			LivingEntity livingEntity = mob.level().getNearestEntity(targetMobs, TargetingConditions.DEFAULT.copy().range(mob.getAttributeValue(Attributes.FOLLOW_RANGE)), mob, mob.getX(), mob.getY(), mob.getZ());
			setTarget(livingEntity);
		}else {
			setTarget(null);
		}
	}
	
	private void setTarget(LivingEntity target){
		this.targetMob = target;
	}
}
