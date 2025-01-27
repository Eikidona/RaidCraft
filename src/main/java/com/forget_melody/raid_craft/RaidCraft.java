package com.forget_melody.raid_craft;

import com.forget_melody.raid_craft.config.Config;
import com.forget_melody.raid_craft.registries.Registries;
import com.forget_melody.raid_craft.world.effect.MobEffects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(RaidCraft.MODID)
public class RaidCraft
{
    public static final String MODID = "raid_craft";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public RaidCraft(FMLJavaModLoadingContext context)
    {
//        LOGGER.log(Level.INFO, "Mod RaidCraft 正在加载");
        IEventBus bus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        Registries.register(bus);
        MobEffects.register(bus);
    }
    
}
