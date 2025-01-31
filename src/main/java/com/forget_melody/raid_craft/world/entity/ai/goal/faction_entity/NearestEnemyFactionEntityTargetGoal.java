package com.forget_melody.raid_craft.world.entity.ai.goal.faction_entity;

import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class NearestEnemyFactionEntityTargetGoal extends NearestAttackableTargetGoal<Mob> {
	public NearestEnemyFactionEntityTargetGoal(Mob pMob) {
		super(pMob, Mob.class, false, target -> target instanceof Mob && IFactionEntity.get(pMob).isHostility((Mob) target));
	}
}
