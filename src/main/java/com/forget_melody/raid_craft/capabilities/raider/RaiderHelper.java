package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public class RaiderHelper {
	public static Optional<IRaider> getRaider(Entity mob) {
		return mob.getCapability(Capabilities.RAIDER).resolve();
	}
}
