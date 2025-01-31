package com.forget_melody.raid_craft.world.effect;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.registries.RaidTargets;
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
	public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
		if (pLivingEntity instanceof ServerPlayer player && !pLivingEntity.isSpectator()) {
			
			ServerLevel level = player.serverLevel();
			if (level.getDifficulty() == Difficulty.PEACEFUL) {
				return;
			}
			
			IRaidInteraction raidInteraction = IRaidInteraction.get(player);
			
			// 如果是村庄立即触发Raid
			if (level.isVillage(pLivingEntity.blockPosition())) {
				if (raidInteraction.getFaction() != null) {
					IRaidManager manager = IRaidManager.get(((ServerPlayer) pLivingEntity).serverLevel());
					manager.createRaid(pLivingEntity.blockPosition(), raidInteraction.getFaction(), RaidTargets.VILLAGE.get());
				} else {
					RaidCraft.LOGGER.error("raidInteraction faction is null");
				}
				pLivingEntity.removeEffect(MobEffects.BAD_OMEN.get());
			}
			
		}
	}
}
