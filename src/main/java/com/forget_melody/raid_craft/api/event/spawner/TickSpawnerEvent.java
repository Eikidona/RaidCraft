package com.forget_melody.raid_craft.api.event.spawner;

import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 当tickCustomSpawners方法被调用时post
 * CustomSpawner尝试生成实体
 */
public class TickSpawnerEvent extends LevelEvent {
	private final List<CustomSpawner> list = new ArrayList<>();
	public TickSpawnerEvent(LevelAccessor level) {
		super(level);
	}
	
	public void addCustomSpawner(CustomSpawner spawner){
		list.add(spawner);
	}
	
	public List<? extends CustomSpawner> getCustomSpawners(){
		return list;
	}
}