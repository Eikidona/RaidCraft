package com.forget_melody.raid_craft.world.entity.ai.brain;

import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.raid.Raid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import org.jetbrains.annotations.NotNull;

public class BeginRaid extends Behavior<Mob> {
	public BeginRaid() {
		// 不检查记忆
		super(ImmutableMap.of());
	}
	
	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, @NotNull Mob owner) {
		return level.random.nextInt(20) == 0;
	}
	
	@Override
	protected void start(@NotNull ServerLevel level, Mob owner, long gametime) {
		IRaidManager manager = IRaidManager.get(level);
		Raid raid = manager.getRaidAtPos(owner.blockPosition());
		
	}
}
