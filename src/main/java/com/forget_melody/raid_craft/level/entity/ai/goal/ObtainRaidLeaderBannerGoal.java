package com.forget_melody.raid_craft.level.entity.ai.goal;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.raid.raid.IRaid;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class ObtainRaidLeaderBannerGoal<T extends Mob> extends Goal {
	private final Mob mob;
	public ObtainRaidLeaderBannerGoal(Mob mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		IRaider raider = IRaider.getRaider(mob).get();
		if(raider.hasActiveRaid()){
			IRaid raid = raider.getRaid();
			if(raid.getLeader() == null){
				List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D), itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && ItemStack.matches(itemEntity.getItem(), raid.getBanner()));
				if (!list.isEmpty()) {
					return this.mob.getNavigation().moveTo(list.get(0), 1.15F);
				}
			}
		}
		return false;
	}
	
	@Override
	public void tick() {
		IRaider raider = IRaider.getRaider(mob).get();
		IRaid raid = raider.getRaid();
		if (mob.getNavigation().getTargetPos().closerToCenterThan(mob.position(), 1.414D)) {
			List<ItemEntity> list = mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D), itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && ItemStack.matches(itemEntity.getItem(), raid.getBanner()));
			if (!list.isEmpty()) {
				list.get(0).discard();
				raider.getRaid().setLeaderRaider(raider);
			}
		}
	}
}
