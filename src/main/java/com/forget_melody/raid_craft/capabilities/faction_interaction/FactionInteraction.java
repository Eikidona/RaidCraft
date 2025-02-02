package com.forget_melody.raid_craft.capabilities.faction_interaction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class FactionInteraction implements IFactionInteraction {
	private final ServerPlayer player;
	private final Map<Faction, Integer> map = new HashMap<>();
	
	public FactionInteraction(ServerPlayer player) {
		this.player = player;
	}
	
	@Override
	public void adjustedAllianceValue(Faction faction, int value) {
		int currentValue = map.computeIfAbsent(faction, fac -> 0);
		map.put(faction, currentValue + value);
		// 同盟阵营 添加value值的一半; 敌对阵营 添加value值的一半的负数
		faction.getEnemyFactions().forEach(enemyFaction -> {
			int currentValue1 = map.computeIfAbsent(enemyFaction, enemy -> 0);
			map.put(enemyFaction, currentValue1 + (value / 2 * -1));
		});
		
		faction.getAllyFactions().forEach(allyFaction -> {
			int currentValue1 = map.computeIfAbsent(allyFaction, ally -> 0);
			map.put(allyFaction, currentValue1 + (value / 2));
		});
	}
	
	@Override
	public void setAllianceValue(Faction faction, int value) {
		map.put(faction, value);
	}
	
	@Override
	public boolean isAlly(Faction faction) {
		return getAllianceValue(faction) >= IFactionInteraction.FRIENDLY;
	}
	
	@Override
	public boolean isEnemy(Faction faction) {
		return getAllianceValue(faction) <= IFactionInteraction.HOSTILITY;
	}
	
	@Override
	public int getAllianceValue(Faction faction) {
		return map.getOrDefault(faction, 0);
	}
	
	@Override
	public ServerPlayer getPlayer() {
		return player;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		ListTag listTag = new ListTag();
		for (Map.Entry<Faction, Integer> entry : map.entrySet()) {
			CompoundTag tag1 = new CompoundTag();
			tag1.putString("Faction", DataPackRegistries.FACTIONS.getKey(entry.getKey()).toString());
			tag.putInt("AllianceValue", entry.getValue());
			listTag.add(tag1);
		}
		tag.put("Factions", listTag);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		ListTag listTag = nbt.getList("Factions", ListTag.TAG_COMPOUND);
		listTag.forEach(tag -> {
			CompoundTag tag1 = (CompoundTag) tag;
			ResourceLocation id = new ResourceLocation(tag1.getString("Faction"));
			Faction faction = DataPackRegistries.FACTIONS.getValue(id);
			if (faction != null) {
				int allianceValue = tag1.getInt("AllianceValue");
				map.put(faction, allianceValue);
			} else {
				RaidCraft.LOGGER.warn("FactionInteraction deserializeNBT: Not found faction id :{}", id);
			}
		});
	}
	
	
}
