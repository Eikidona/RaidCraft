package com.forget_melody.raid_craft.raid.raid.target;

import com.forget_melody.raid_craft.raid.raid.Raid;
import net.minecraft.core.BlockPos;

import java.util.Optional;

public interface IRaidTarget {

	Optional<BlockPos> updateTargetPos(Raid raid);
	
	boolean isValidTarget(Raid raid);
	
	int getTargetStrength(Raid raid);
}
