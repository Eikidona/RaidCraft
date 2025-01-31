package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.boost.EquipmentBoost;
import com.forget_melody.raid_craft.boost.IBoost;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;

public class Boosts extends ReloadListener<IBoost> {
	
	public Boosts() {
		super("boost", Boosts::createFromJson);
	}
	
	@Nullable
	public static IBoost createFromJson(JsonElement jsonElement){
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		IBoost boost = null;
		if(jsonObject.get("type").getAsString().equals("equipment")){
			boost = EquipmentBoost.CODEC.parse(JsonOps.INSTANCE, jsonObject)
					.resultOrPartial(string -> RaidCraft.LOGGER.error("Failed to parse JSON for EquipmentBoost"))
					.get();
		}
		return boost;
	}
}
