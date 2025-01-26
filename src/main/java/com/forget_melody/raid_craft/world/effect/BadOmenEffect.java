package com.forget_melody.raid_craft.world.effect;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BadOmenEffect extends MobEffect {
	public BadOmenEffect() {
		super(MobEffectCategory.NEUTRAL, 745784);
	}
	
	@Override
	public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
		return true;
	}
	
	@Override
	public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
		if (pLivingEntity instanceof ServerPlayer && !pLivingEntity.isSpectator()) {
			
			ServerLevel level = ((ServerPlayer) (pLivingEntity)).serverLevel();
			if (level.getDifficulty() == Difficulty.PEACEFUL) {
				return;
			}
			
			IRaidInteraction raidInteraction = IRaidInteraction.get((ServerPlayer) pLivingEntity).get();
			
			// 如果是村庄立即触发Raid
			if (level.isVillage(pLivingEntity.blockPosition())) {
				if (raidInteraction.getFaction() != null) {
					IRaidManager manager = IRaidManager.get(((ServerPlayer) pLivingEntity).serverLevel()).get();
					manager.createRaid(pLivingEntity.blockPosition(), raidInteraction.getFaction());
				} else {
					RaidCraft.LOGGER.error("raidInteraction faction is null");
				}
				pLivingEntity.removeEffect(MobEffects.BAD_OMEN.get());
			}
			
		}
	}
}
