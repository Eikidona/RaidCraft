package com.forget_melody.raid_craft.raid.patrol;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.forget_melody.raid_craft.utils.weight_table.IWeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.IWeightTable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 巡逻队 检查实体存活性 当队内全部实体都死亡时 该队在300tick后解散
 */
public class Patrol {
	private final ServerLevel level;
	private boolean started;
	private boolean stopped;
	private final int id;
	private int activeTicks;
	private BlockPos originPos;
	private BlockPos patrolTarget;
	private int patrolCoolDownTicks = 200;
	private final Faction faction;
	private IPatroller leader;
	private final List<IPatroller> patrollers = new ArrayList<>();
	
	public Patrol(int id, ServerLevel level, Faction faction, BlockPos originPos) {
		this.id = id;
		this.level = level;
		this.faction = faction;
		this.originPos = originPos;
	}
	
	public Patrol(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.id = tag.getInt("Id");
		this.started = tag.getBoolean("Start");
		this.activeTicks = tag.getInt("ActiveTicks");
		if(tag.contains("OriginPos")){
			this.originPos = NbtUtils.readBlockPos(tag.getCompound("OriginPos"));
		}
		if (tag.contains("PatrolTarget")) {
			this.patrolTarget = NbtUtils.readBlockPos(tag.getCompound("PatrolTarget"));
		}
		net.minecraft.resources.ResourceLocation factionId = new net.minecraft.resources.ResourceLocation(tag.getString("Faction"));
		Faction faction1 = DataPackRegistries.FACTIONS.getValue(factionId);
		if (faction1 == null) {
			RaidCraft.LOGGER.error("Not found faction id {}", factionId);
			this.stopped = true;
		}
		this.faction = faction1;
	}
	
	@Nullable
	public IPatroller getLeader() {
		return leader;
	}
	
	public void setLeader(IPatroller leader) {
		this.leader = leader;
		leader.setPatrolLeader(true);
	}
	
	private void equipBanner(IPatroller leader) {
		Mob mob = leader.getMob();
		mob.setItemSlot(EquipmentSlot.HEAD, faction.getBanner());
		mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
	}
	
	public int getNumPatrollers() {
		return patrollers.size();
	}
	
	public List<IPatroller> getPatrollers() {
		return patrollers;
	}
	
	public BlockPos getOriginPos() {
		return originPos;
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * 检查实体是否存活 更新Leader字段状态
	 */
	private void updatePatroller() {
		Iterator<IPatroller> iterator = patrollers.iterator();
		while (iterator.hasNext()) {
			IPatroller patroller = iterator.next();
			if (!patroller.getMob().isAlive()) {
				iterator.remove();
				if (patroller.isPatrolLeader()) {
					leader = null;
				}
			}
		}
	}
	
	public void tick() {
		if (isStopped()) return;
		activeTicks++;
		if (!started) {
			start();
		}
		
		if(activeTicks % 20 == 0){
			updatePatroller();
		}
		if (patrollers.isEmpty()) {
			if (patrolCoolDownTicks == 0) {
				stop();
			} else {
				patrolCoolDownTicks--;
			}
		}else {
			patrolCoolDownTicks = 200;
		}
	}
	
	private void start() {
		started = true;
		spawnPatroller();
		IPatroller patroller = patrollers.get(0);
		setLeader(patroller);
		equipBanner(patroller);
	}
	
	private void spawnPatroller() {
		if (faction.getFactionEntityTypes().isEmpty()) {
			RaidCraft.LOGGER.warn("faction {} is not have any FactionEntityType", DataPackRegistries.FACTIONS.getKey(faction));
			return;
		}
		
		RandomSource random = level.getRandom();
		BlockPos.MutableBlockPos pos = originPos.mutable();
		int numBaseSpawn = level.getDifficulty().getId() * 2;
		int numTotalSpawn = (int) Math.ceil(level.getCurrentDifficultyAt(pos).getEffectiveDifficulty()) + numBaseSpawn;
		IWeightTable<PatrollerType> weightTable = IWeightTable.of(faction.getPatrolConfig().getPatrollerTypeList().stream().map(patrollerType -> IWeightEntry.of(patrollerType, patrollerType.getWeight())).toList());
		for (int numCurrentSpawn = 0; numCurrentSpawn < numTotalSpawn; numCurrentSpawn++) {
			
			pos.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY());
			pos.setX(pos.getX() + random.nextInt(10) - 5);
			pos.setZ(pos.getZ() + random.nextInt(10) - 5);
			// 已经检查过列表是否为空 这不需要再判断权重表是否为空
			IPatroller patroller = weightTable.getElement().spawn(level, pos, this);
			if (patroller == null) {
				continue;
			}
			joinPatrol(patroller);
		}
		
		if (patrollers.isEmpty()) {
			RaidCraft.LOGGER.warn("no patroller spawned");
		}
	}
	
	public Faction getFaction() {
		return faction;
	}
	
	@Nullable
	public BlockPos getPatrolTarget() {
		return patrolTarget;
	}
	
	public void updatePatrolTarget(BlockPos patrolTarget) {
		this.patrolTarget = patrolTarget;
		this.patrollers.forEach(patroller -> patroller.setPatrolling(true));
	}
	
	public void joinPatrol(IPatroller patroller) {
		this.patrollers.add(patroller);
		patroller.setPatrol(this);
	}
	
	public void quitPatrol(IPatroller patroller) {
		this.patrollers.remove(patroller);
		if (patroller.isPatrolLeader()) {
			this.leader = null;
		}
		patroller.setPatrolling(false);
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void stop() {
		this.stopped = true;
		RaidCraft.LOGGER.info("巡逻队Stop");
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Id", this.id);
		tag.putBoolean("Start", this.started);
		tag.putString("Faction", DataPackRegistries.FACTIONS.getKey(this.faction).toString());
		tag.putInt("ActiveTicks", this.activeTicks);
		if(this.originPos != null){
			tag.put("OriginPos", NbtUtils.writeBlockPos(this.originPos));
		}
		if (this.patrolTarget != null) {
			tag.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
		}
		
		return tag;
	}
}
