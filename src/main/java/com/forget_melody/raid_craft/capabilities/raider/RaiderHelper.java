package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

public class RaiderHelper {
	public static Optional<IRaider> getRaider(Mob mob) {
		IRaider raider = mob.getCapability(Capabilities.RAIDER).orElse(Raider.EMPTY);
		return raider == Raider.EMPTY ? Optional.empty() : Optional.of(raider);
	}
}
