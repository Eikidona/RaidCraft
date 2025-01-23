package com.forget_melody.raid_craft.mixin;

import com.forget_melody.raid_craft.event.spawner.TickSpawnerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = ServerLevel.class, priority = 1000)
public abstract class ServerLevelMixin {

	@Inject(method = "tickCustomSpawners", at = @At(value = "HEAD"))
	public void tickCustomSpawners(boolean pSpawnEnemies, boolean pSpawnFriendlies, CallbackInfo ci) {
		TickSpawnerEvent event = new TickSpawnerEvent((ServerLevel)(Object)this);
		MinecraftForge.EVENT_BUS.post(event);
		event.getCustomSpawners().forEach(customSpawner -> customSpawner.tick((ServerLevel)(Object)this, pSpawnEnemies, pSpawnFriendlies));
	}
}
