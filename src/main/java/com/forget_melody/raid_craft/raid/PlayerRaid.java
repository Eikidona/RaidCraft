package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.faction.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PlayerRaid extends Raid{
	private ServerPlayer targetPlayer;
	public PlayerRaid(int id, ServerLevel level, Faction faction, BlockPos center) {
		super(id, level, faction, center);
	}
	
	public PlayerRaid(ServerLevel level, CompoundTag tag) {
		super(level, tag);
	}
	
	@Override
	protected RaidType getType() {
		return RaidType.PLAYER;
	}
	
	@Override
	protected boolean checkLoseCondition() {
		return targetPlayer == null || !targetPlayer.isAlive();
	}
	
	@Override
	protected void updateTargetPos() {
		getPLayers().stream().findFirst().ifPresent(player -> {
			targetPlayer = player;
			setCenter(targetPlayer.blockPosition());
		});
	}
	
	@Override
	protected int computedStrength() {
		int baseStrength = 0;
		AttributeInstance armor = targetPlayer.getAttribute(Attributes.ARMOR);
		AttributeInstance maxHealth = targetPlayer.getAttribute(Attributes.MAX_HEALTH);
		AttributeInstance attackDamage = targetPlayer.getAttribute(Attributes.ATTACK_DAMAGE);
		if(armor != null){
			baseStrength += (int) armor.getValue();
		}
		if(maxHealth != null){
			if(maxHealth.getValue() > 20){
				baseStrength += (int) maxHealth.getValue() - 20;
			}
		}
		if(attackDamage != null){
			baseStrength += (int) attackDamage.getValue();
		}
		return baseStrength;
	}
}
