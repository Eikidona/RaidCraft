package com.forget_melody.raid_craft.world.entity.ai.goal.faction_entity;

import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.faction_interaction.IFactionInteraction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class NearestEnemyFactionEntityTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
	public NearestEnemyFactionEntityTargetGoal(Mob mob) {
		super(mob, LivingEntity.class, false, entity -> {
			if (entity instanceof Mob target1) {
				return IFactionEntity.get(mob).isEnemy(IFactionEntity.get(target1).getFaction());
			} else if (entity instanceof ServerPlayer player) {
				return IFactionInteraction.get(player).isEnemy(IFactionEntity.get(mob).getFaction());
			}
			return false;
		});
	}
}
