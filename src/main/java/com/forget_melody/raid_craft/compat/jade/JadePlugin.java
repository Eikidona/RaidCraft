package com.forget_melody.raid_craft.compat.jade;

import com.forget_melody.raid_craft.RaidCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
	public static final ResourceLocation FACTION_ENTITY = new ResourceLocation(RaidCraft.MOD_ID, "faction_entity");
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerEntityDataProvider(new FactionEntityJadePlugin(), Mob.class);
	}
	
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(new FactionEntityJadePlugin(), Mob.class);
	}
	
	
}
