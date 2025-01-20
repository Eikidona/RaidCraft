package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

public class FactionEntityHelper {
	public static Optional<IFactionEntity> getFactionEntity(Mob mob) {
		return mob.getCapability(Capabilities.FACTION_ENTITY).resolve();
	}
}
