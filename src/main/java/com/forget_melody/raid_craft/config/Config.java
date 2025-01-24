package com.forget_melody.raid_craft.config;

import com.forget_melody.raid_craft.RaidCraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
public class Config {
	
	public static ForgeConfigSpec.IntValue PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
	public static ForgeConfigSpec.IntValue PATROL_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
	public static ForgeConfigSpec.IntValue PATROL_DAYTIME_BEFORE_SPAWNING;
	public static ForgeConfigSpec.DoubleValue PATROL_SPAWN_CHANCE_ON_SPAWN_ATTEMPT;
	
	
	
	public static class Common {
		public Common(ForgeConfigSpec.Builder builder) {
			PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
															   .comment("""
                    巡逻队生成延时，此值加上巡逻队生成随机延时，一起构成两次刷出尝试之间的时间。参考:原版巡逻队默认值12000，Mod默认值6000.
                    This value plus the variable version together make up the time between two spawn attempts. \\n" +
                                                "Vanilla default 12000, Default 6000""")
															   .defineInRange("patrolTickDelayBetweenSpawnAttempts", 6000, 0, Integer.MAX_VALUE);
			PATROL_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
																		.comment("""
																													 巡逻队生成随机延时，参考：原版1200，默认1200
																													 A random value between 0 and this value is added to the static delay to determine total delay.\s
																													 Vanilla default is 1200. Default 1200""")
																		.defineInRange("patrolVariableTickDelayBetweenSpawnAttempts", 1200, 0, Integer.MAX_VALUE);
			PATROL_DAYTIME_BEFORE_SPAWNING = builder
													 .comment("ingame daytime before patrols start spawning. \n" +
																	  "Vanilla is 24000L equivalent of 5days. Default 24000L")
													 .defineInRange("patrolDaytimeBeforeSpawning", 24000, 0, Integer.MAX_VALUE);
			PATROL_SPAWN_CHANCE_ON_SPAWN_ATTEMPT = builder
														   .comment("The chance a patrol spawns on a spawn attempt. \n" +
																			"Vanilla default is 0.2 Default 0.3")
														   .defineInRange("patrolSpawnChanceOnSpawnAttempt", 0.3, 0.0, 1.0);
		}
	}
	
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	static {
		final Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = pair.getLeft();
		COMMON_SPEC = pair.getRight();
	}

}
