/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.jump.listeners;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import nz.co.lolnet.jump.Jump;
import nz.co.lolnet.jump.configuration.Config;
import nz.co.lolnet.jump.data.JumpData;
import nz.co.lolnet.jump.event.ChangeFlyEvent;
import nz.co.lolnet.jump.managers.JumpManager;
import nz.co.lolnet.jump.util.Toolbox;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class JumpListener {
    
    @Listener
    public void onChangeFly(ChangeFlyEvent event, @Getter("getTargetEntity") Player player) {
        GameMode gamemode = player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET);
        if (!event.isFlying() || (gamemode != GameModes.ADVENTURE && gamemode != GameModes.SURVIVAL)) {
            return;
        }
        
        event.setCancelled(true);
        Optional<JumpData> jumpData = player.get(JumpData.class);
        if (!jumpData.isPresent() || jumpData.get().getCount() < 100) {
            return;
        }
        
        jumpData.get().setCount(0);
        player.offer(jumpData.get());
        JumpManager.updateBossBar(player, 0.0F);
        Optional<Double> jumpLevel = getJumpLevel(player);
        if (jumpLevel.isPresent() && jumpLevel.get() > 0.0D) {
            Vec3d vector = ((Entity) player).getLookVec().scale(jumpLevel.get());
            player.setVelocity(player.getVelocity().add(vector.x, jumpLevel.get(), vector.z));
        }
    }
    
    @Listener
    public void onClientConnectionDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        JumpManager.removeBossBar(player);
        GameMode gamemode = player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET);
        if (gamemode != GameModes.ADVENTURE && gamemode != GameModes.SURVIVAL) {
            return;
        }
        
        player.offer(Keys.CAN_FLY, false);
    }
    
    @Listener
    public void onClientConnectionJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        JumpManager.createBossBar(player);
        Optional<JumpData> jumpData = player.get(JumpData.class);
        if (!jumpData.isPresent()) {
            return;
        }
        
        JumpManager.updateBossBar(player, (float) jumpData.get().getCount() / (float) 100);
    }
    
    @Listener
    public void onDamageEntity(DamageEntityEvent event, @Root DamageSource damageSource, @Getter("getTargetEntity") Player player) {
        event.setCancelled(damageSource.getType().equals(DamageTypes.FALL) && player.hasPermission("jump.nofalldamage"));
    }
    
    @Listener
    public void onMoveEntity(MoveEntityEvent event, @Getter("getTargetEntity") Player player) {
        GameMode gamemode = player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET);
        if (gamemode != GameModes.ADVENTURE && gamemode != GameModes.SURVIVAL) {
            return;
        }
        
        Location<World> from = event.getFromTransform().getLocation();
        Location<World> to = event.getToTransform().getLocation();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ() && from.getExtent() == to.getExtent()) {
            return;
        }
        
        if (from.getBlockY() > to.getBlockY() && to.getBlockY() < 0) {
            Optional<Double> voidJumpLevel = Jump.getInstance().getConfig().map(Config::getVoidJumpLevel);
            if (voidJumpLevel.isPresent() && voidJumpLevel.get() > 0.0D) {
                player.setVelocity(player.getVelocity().max(Double.MIN_VALUE, voidJumpLevel.get(), Double.MIN_VALUE));
            }
            
            return;
        }
        
        JumpData jumpData = player.get(JumpData.class).orElse(new JumpData(0));
        if (jumpData.getCount() >= 100) {
            player.offer(Keys.CAN_FLY, true);
            JumpManager.updateBossBar(player, 1.0F);
        } else if (player.isOnGround() || player.getLocation().getBlockRelative(Direction.DOWN).getBlockType() != BlockTypes.AIR) {
            jumpData.setCount(jumpData.getCount() + 1);
            player.offer(jumpData);
            player.offer(Keys.CAN_FLY, false);
            JumpManager.updateBossBar(player, (float) jumpData.getCount() / (float) 100);
        }
    }
    
    private Optional<Double> getJumpLevel(Player player) {
        Optional<String> option = Toolbox.getOptionFromSubject(player, "jump-level");
        if (option.isPresent()) {
            Optional<Double> jumpLevel = option.flatMap(Toolbox::parseDouble);
            if (jumpLevel.isPresent()) {
                return jumpLevel;
            }
            
            Jump.getInstance().getLogger().warn("Failed to parse jump level for {} ({})", player.getName(), player.getUniqueId());
        }
        
        return Jump.getInstance().getConfig().map(Config::getDefaultJumpLevel);
    }
}