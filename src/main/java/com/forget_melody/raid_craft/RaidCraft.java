package com.forget_melody.raid_craft;

import com.forget_melody.raid_craft.config.Config;
import com.forget_melody.raid_craft.registries.RaidTargets;
import com.forget_melody.raid_craft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(RaidCraft.MOD_ID)
public class RaidCraft
{
    public static final String MOD_ID = "raid_craft";
    public static final Logger LOGGER = LogManager.getLogger();
	public static final ResourceLocation DEFAULT_KEY = new ResourceLocation(RaidCraft.MOD_ID, "default");
	
	public RaidCraft(FMLJavaModLoadingContext context)
    {
//        LOGGER.log(Level.INFO, "Mod RaidCraft 正在加载");
        IEventBus bus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
		RaidTargets.register(bus);
        MobEffects.register(bus);
    }
    
}
