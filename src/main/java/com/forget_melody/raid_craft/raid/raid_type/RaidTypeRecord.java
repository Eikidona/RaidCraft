package com.forget_melody.raid_craft.raid.raid_type;

import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.utils.weight_table.IWeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.WeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.WeightTable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

import java.util.List;

public record RaidTypeRecord(String raidDisplay, String winDisplay, String loseDisplay, String color, String overlay, List<WeightEntry<ResourceLocation>> raiders) {
	public static final Codec<RaidTypeRecord> CODEC = RecordCodecBuilder.create(raidTypeRecordInstance -> raidTypeRecordInstance.group(
					Codec.STRING.fieldOf("raidDisplay").forGetter(RaidTypeRecord::raidDisplay),
					Codec.STRING.fieldOf("winDisplay").forGetter(RaidTypeRecord::winDisplay),
					Codec.STRING.fieldOf("loseDisplay").forGetter(RaidTypeRecord::loseDisplay),
					Codec.STRING.fieldOf("color").forGetter(RaidTypeRecord::color),
					Codec.STRING.fieldOf("overlay").forGetter(RaidTypeRecord::overlay),
					Codec.list(WeightEntry.createCodec(ResourceLocation.CODEC)).fieldOf("raiders").forGetter(RaidTypeRecord::raiders)
			).apply(raidTypeRecordInstance, RaidTypeRecord::new)
	);
	
}
