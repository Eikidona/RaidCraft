package com.forget_melody.raid_craft.raid.raid.target;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.raid.raid.Raid;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.MoveTowardsRaidGoal;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;

public interface IRaidTarget {
	
	void updateTargetPos(Raid raid);
	
	boolean checkLoseCondition(Raid raid);
	
	int getTargetStrength(Raid raid);
	
	default int getTotalWave(Raid raid){
		int baseWave = 0;
		if(raid.getLevel().getDifficulty() == Difficulty.EASY){
			baseWave += raid.getLevel().getRandom().nextInt(3) + 1;
		}else if(raid.getLevel().getDifficulty() == Difficulty.NORMAL){
			baseWave += raid.getLevel().getRandom().nextInt(5) + 1;
		}else if(raid.getLevel().getDifficulty() == Difficulty.HARD){
			baseWave += raid.getLevel().getRandom().nextInt(7) + 1;
		}
		
		DifficultyInstance instance = raid.getLevel().getCurrentDifficultyAt(raid.getCenter());
		baseWave += (int) (instance.getEffectiveDifficulty());
		return baseWave;
	}
	
	default void addGoal(IRaider raider){
		raider.addGoal(5, new MoveTowardsRaidGoal<>(raider.getMob()));
	};
}
