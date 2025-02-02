package com.forget_melody.raid_craft.world.effect;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.raid.RaidType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BadOmenEffect extends MobEffect {
	public BadOmenEffect() {
		super(MobEffectCategory.NEUTRAL, 745784);
	}
	
	@Override
	public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
		return true;
	}
	
	@Override
	public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
		if (livingEntity instanceof ServerPlayer player && !livingEntity.isSpectator()) {
			
			ServerLevel level = player.serverLevel();
			if (level.getDifficulty() == Difficulty.PEACEFUL) {
				return;
			}
			
			IRaidInteraction raidInteraction = IRaidInteraction.get(player);
			
			// 如果是村庄立即触发Village Target Raid
			BlockPos pos = livingEntity.blockPosition();
			if (level.isVillage(pos)) {
				if (raidInteraction.getFaction() != null) {
					IRaidManager manager = IRaidManager.get(((ServerPlayer) livingEntity).serverLevel());
					manager.createRaid(pos, raidInteraction.getFaction(), RaidType.VILLAGE);
				} else {
					RaidCraft.LOGGER.error("raidInteraction faction is null");
				}
				livingEntity.removeEffect(MobEffects.BAD_OMEN.get());
			}
			
		}
	}
}
