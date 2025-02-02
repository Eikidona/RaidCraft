package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.boost.BoostType;
import com.forget_melody.raid_craft.boost.EquipmentBoost;
import com.forget_melody.raid_craft.boost.IBoost;
import com.forget_melody.raid_craft.boost.MountBoost;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;

public class Boosts extends ReloadListener<IBoost> {
	
	public Boosts() {
		super("boost", Boosts::createFromJson);
	}
	
	@Nullable
	public static IBoost createFromJson(JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		IBoost boost = null;
		BoostType type = BoostType.byName(jsonObject.get("type").getAsString());
		switch (type) {
			case EQUIPMENT -> boost = EquipmentBoost.CODEC.parse(JsonOps.INSTANCE, jsonObject)
														  .resultOrPartial(string -> RaidCraft.LOGGER.error("Failed to parse JSON for {}", type))
														  .get();
			
			case MOUNT -> boost = MountBoost.CODEC.parse(JsonOps.INSTANCE, jsonObject)
												  .resultOrPartial(string -> RaidCraft.LOGGER.error("Failed to parse JSON for {}", type))
												  .get();
		}
		return boost;
	}
}
