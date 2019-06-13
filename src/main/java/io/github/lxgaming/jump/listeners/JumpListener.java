/*
 * Copyright 2018 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.jump.listeners;

import com.flowpowered.math.vector.Vector3d;
import io.github.lxgaming.jump.Jump;
import io.github.lxgaming.jump.configuration.Config;
import io.github.lxgaming.jump.data.JumpData;
import io.github.lxgaming.jump.data.JumpDataBuilder;
import io.github.lxgaming.jump.event.ChangeFlyEvent;
import io.github.lxgaming.jump.managers.JumpManager;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.ChangeGameModeEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class JumpListener {
    
    @Listener
    public void onChangeFly(ChangeFlyEvent event, @Getter("getTargetEntity") Player player) {
        if (event.isCancelled() || !player.get(Keys.GAME_MODE).map(JumpManager::isValidGameMode).orElse(false)) {
            return;
        }
        
        if (event.isElytra()) {
            player.offer(Keys.CAN_FLY, !event.isFlying());
            JumpManager.updateBossBar(player, !event.isFlying());
            return;
        }
        
        event.setCancelled(true);
        if (!event.isFlying()) {
            return;
        }
        
        int capacity = JumpManager.getCapacity(player).orElse(-1);
        int charge = player.get(JumpData.CHARGE_KEY).orElse(-1);
        if (capacity < 0 || charge < 0 || charge < capacity) {
            return;
        }
        
        player.offer(Keys.CAN_FLY, false);
        player.offer(JumpData.CHARGE_KEY, 0);
        JumpManager.updateBossBar(player, 0.0F);
        double multiplier = JumpManager.getMultiplier(player).orElse(0.0D);
        if (multiplier > 0.0D) {
            Vector3d vector = JumpManager.getRotationVector(player.getRotation()).mul(multiplier);
            player.setVelocity(player.getVelocity().add(vector.getX(), multiplier, vector.getZ()));
        }
    }
    
    @Listener
    public void onChangeGameMode(ChangeGameModeEvent event, @Getter("getTargetEntity") Player player) {
        if (event.isCancelled() || player.get(Keys.IS_ELYTRA_FLYING).orElse(false)) {
            return;
        }
        
        JumpManager.updateBossBar(player, JumpManager.isValidGameMode(event.getGameMode()));
    }
    
    @Listener
    public void onClientConnectionDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        JumpManager.removeBossBar(player);
        if (player.get(Keys.GAME_MODE).map(JumpManager::isValidGameMode).orElse(false)) {
            player.offer(Keys.CAN_FLY, false);
            player.offer(Keys.IS_FLYING, false);
        }
    }
    
    @Listener
    public void onClientConnectionJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        JumpManager.createBossBar(player);
        
        JumpData jumpData = player.get(JumpData.class).orElse(JumpDataBuilder.builder().create());
        if (jumpData.getCharge() < 0) {
            jumpData.setCharge(0);
            Jump.getInstance().getLogger().warn("Invalid charge for {} ({})", player.getName(), player.getUniqueId());
        }
        
        player.offer(jumpData);
        
        int capacity = JumpManager.getCapacity(player).orElse(-1);
        int charge = player.get(JumpData.CHARGE_KEY).orElse(-1);
        if (capacity < 0 || charge < 0) {
            return;
        }
        
        JumpManager.updateBossBar(player, JumpManager.getPercent(charge, capacity));
        JumpManager.updateBossBar(player, player.get(Keys.GAME_MODE).map(JumpManager::isValidGameMode).orElse(false));
    }
    
    @Listener
    public void onDamageEntity(DamageEntityEvent event, @Root DamageSource damageSource, @Getter("getTargetEntity") Player player) {
        if (event.isCancelled()) {
            return;
        }
        
        event.setCancelled(damageSource.getType().equals(DamageTypes.FALL) && player.hasPermission("jump.nofalldamage"));
    }
    
    @Listener
    public void onMoveEntity(MoveEntityEvent event, @Getter("getTargetEntity") Player player) {
        if (event.isCancelled() || !player.get(Keys.GAME_MODE).map(JumpManager::isValidGameMode).orElse(false) || player.get(Keys.IS_ELYTRA_FLYING).orElse(false)) {
            return;
        }
        
        Location<World> from = event.getFromTransform().getLocation();
        Location<World> to = event.getToTransform().getLocation();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ() && from.getExtent() == to.getExtent()) {
            return;
        }
        
        if (from.getBlockY() > to.getBlockY() && to.getBlockY() < 0) {
            double voidMultiplier = Jump.getInstance().getConfig().map(Config::getVoidMultiplier).orElse(0.0D);
            if (voidMultiplier > 0.0D) {
                player.setVelocity(player.getVelocity().max(Double.MIN_VALUE, voidMultiplier, Double.MIN_VALUE));
            }
            
            return;
        }
        
        if (to.getBlockY() > to.getExtent().getDimension().getBuildHeight()) {
            return;
        }
        
        int capacity = JumpManager.getCapacity(player).orElse(-1);
        int charge = player.get(JumpData.CHARGE_KEY).orElse(-1);
        if (capacity < 0 || charge < 0) {
            return;
        }
        
        if (charge >= capacity) {
            player.offer(Keys.CAN_FLY, true);
            JumpManager.updateBossBar(player, 1.0F);
        } else if (player.isOnGround() || player.getLocation().getBlockRelative(Direction.DOWN).getBlockType() != BlockTypes.AIR) {
            player.offer(Keys.CAN_FLY, false);
            player.offer(JumpData.CHARGE_KEY, charge + 1);
            JumpManager.updateBossBar(player, JumpManager.getPercent(charge, capacity));
        }
    }
}