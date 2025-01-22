package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.registries.datapack.api.Internal.IRegistry;
import org.jetbrains.annotations.Nullable;

public interface IReMapRegistry<T, K> extends IRegistry<T> {
	@Nullable T getReMapValue(K key);
	
}
