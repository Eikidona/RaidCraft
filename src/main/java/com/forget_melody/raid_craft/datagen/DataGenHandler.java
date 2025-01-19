package com.forget_melody.raid_craft.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DataGenHandler {
	
	@SubscribeEvent
	public static void dataGen(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		if(event.includeServer()){
		
		}
		if(event.includeClient()){
		
		}
	}
}
