package com.forget_melody.raid_craft.mixin;

import com.forget_melody.raid_craft.api.event.spawner.TickSpawnerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLevel.class, priority = 1000)
public abstract class ServerLevelMixin {
	@Inject(method = "tickCustomSpawners", at = @At(value = "HEAD"))
	public void tickCustomSpawners(boolean pSpawnEnemies, boolean pSpawnFriendlies, CallbackInfo ci){
		MinecraftForge.EVENT_BUS.post(new TickSpawnerEvent((LevelAccessor) this));
	}
}
