package com.forget_melody.raid_craft.raid.raid.target;

import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.raid.raid.Raid;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PlayerRaidTarget implements IRaidTarget {
	private ServerPlayer targetPlayer;
	
	public void updateTargetPos(Raid raid) {
		if (targetPlayer == null || !targetPlayer.isAlive()) {
			Optional<ServerPlayer> optional = raid.getPLayers().stream().findFirst();
			optional.ifPresent(player -> {
				targetPlayer = player;
				raid.setCenter(targetPlayer.blockPosition());
			});
		}else if(targetPlayer != null && targetPlayer.isAlive()){
			raid.setCenter(targetPlayer.blockPosition());
		}
	}
	
	@Override
	public boolean checkLoseCondition(Raid raid) {
		return targetPlayer == null || !targetPlayer.isAlive() || raid.getCenter().distSqr(targetPlayer.blockPosition()) >= 256;
	}
	
	@Override
	public int getTargetStrength(Raid raid) {
		return IRaidInteraction.get(targetPlayer).getStrength();
	}
	
}
