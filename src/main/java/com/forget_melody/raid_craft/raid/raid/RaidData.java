package com.forget_melody.raid_craft.raid.raid;

import net.minecraft.nbt.CompoundTag;

/**
 * Raid的数据
 */
public class RaidData {
	private final int id;
	float totalHealth = 0;
	private float health = 0;
	private int celebrateTicks = 600;
	private int raidCooldown = 300;
	private int activeTicks = 0;
	private boolean started = false;
	private boolean victory = false;
	private boolean defeat = false;
	private boolean stopped = false;
	
	public RaidData(int id, int raidCooldown, int celebrateTicks) {
		this.id = id;
		this.raidCooldown = raidCooldown;
		this.celebrateTicks = celebrateTicks;
	}
	
	public RaidData(CompoundTag tag) {
		this.id = tag.getInt("Id");
		this.totalHealth = tag.getInt("TotalHealth");
		this.health = tag.getInt("Health");
		this.raidCooldown = tag.getInt("RaidCooldown");
		this.activeTicks = tag.getInt("ActiveTicks");
		this.started = tag.getBoolean("Started");
		this.victory = tag.getBoolean("Victory");
		this.defeat = tag.getBoolean("Defeat");
		this.stopped = tag.getBoolean("Stopped");
		this.celebrateTicks = tag.getInt("CelebrateTicks");
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Id", this.id);
		tag.putFloat("TotalHealth", this.totalHealth);
		tag.putFloat("Health", this.health);
		tag.putInt("RaidCooldown", this.raidCooldown);
		tag.putInt("CelebrateTicks", this.celebrateTicks);
		tag.putFloat("ActiveTicks", this.health);
		tag.putBoolean("Started", this.started);
		tag.putBoolean("Victory", this.victory);
		tag.putBoolean("Defeat", this.defeat);
		tag.putBoolean("Stopped", this.started);
		return tag;
	}
	
	public float getTotalHealth() {
		return totalHealth;
	}
	
	public void setTotalHealth(float totalHealth) {
		this.totalHealth = totalHealth;
	}
	
	public float getHealth() {
		return health;
	}
	
	public void setHealth(float health) {
		this.health = health;
	}
	
	public int getCelebrateTicks() {
		return celebrateTicks;
	}
	
	public void setCelebrateTicks(int celebrateTicks) {
		this.celebrateTicks = celebrateTicks;
	}
	
	public int getRaidCooldown() {
		return raidCooldown;
	}
	
	public void setRaidCooldown(int raidCooldown) {
		this.raidCooldown = raidCooldown;
	}
	
	public int getActiveTicks() {
		return activeTicks;
	}
	
	public void setActiveTicks(int activeTicks) {
		this.activeTicks = activeTicks;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void setStarted(boolean started) {
		this.started = started;
	}
	
	public boolean isVictory() {
		return victory;
	}
	
	public void setVictory(boolean victory) {
		this.victory = victory;
	}
	
	public boolean isDefeat() {
		return defeat;
	}
	
	public void setDefeat(boolean defeat) {
		this.defeat = defeat;
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
	
	public int getId() {
		return id;
	}
}
