package com.forget_melody.raid_craft.raid.raid.raid_strategy;

import com.forget_melody.raid_craft.raid.raid.AbstractRaid;

public interface IRaidStrategy {
	boolean stopCondition(AbstractRaid raid);
	boolean startCondition(AbstractRaid raid);
	boolean victoryCondition(AbstractRaid raid);
	boolean defeatCondition(AbstractRaid raid);
	boolean spawnCondition(AbstractRaid raid);
}
