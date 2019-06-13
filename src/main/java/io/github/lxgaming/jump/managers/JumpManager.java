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

package io.github.lxgaming.jump.managers;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import io.github.lxgaming.jump.Jump;
import io.github.lxgaming.jump.configuration.Config;
import io.github.lxgaming.jump.util.Reference;
import io.github.lxgaming.jump.util.Toolbox;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class JumpManager {
    
    private static final Map<UUID, ServerBossBar> BOSS_BARS = Toolbox.newHashMap();
    
    public static boolean createBossBar(Player player) {
        ServerBossBar serverBossBar = ServerBossBar.builder()
                .visible(false)
                .playEndBossMusic(false)
                .percent(0.0F)
                .overlay(BossBarOverlays.PROGRESS)
                .name(Jump.getInstance().getConfig().map(Config::getBossBarTitle).map(Toolbox::convertColor).orElse(Text.EMPTY))
                .darkenSky(false)
                .createFog(false)
                .color(BossBarColors.RED)
                .build();
        
        serverBossBar.addPlayer(player);
        
        return getBossBars().put(player.getUniqueId(), serverBossBar) == null;
    }
    
    public static boolean removeBossBar(Player player) {
        ServerBossBar serverBossBar = getBossBars().remove(player.getUniqueId());
        if (serverBossBar != null) {
            serverBossBar.setVisible(false);
            serverBossBar.setPercent(0.0F);
            serverBossBar.setColor(BossBarColors.RED);
            serverBossBar.removePlayer(player);
            return true;
        }
        
        return false;
    }
    
    public static void updateBossBar(Player player, float percent) {
        ServerBossBar serverBossBar = getBossBar(player).orElse(null);
        if (serverBossBar == null || serverBossBar.getPercent() == percent) {
            return;
        }
        
        serverBossBar.setPercent(percent);
        if (serverBossBar.getPercent() >= 1.0F) {
            serverBossBar.setColor(BossBarColors.GREEN);
        } else if (serverBossBar.getPercent() >= 0.5F) {
            serverBossBar.setColor(BossBarColors.YELLOW);
        } else if (serverBossBar.getPercent() >= 0.0F) {
            serverBossBar.setColor(BossBarColors.RED);
        }
    }
    
    public static void updateBossBar(Player player, boolean visible) {
        ServerBossBar serverBossBar = getBossBar(player).orElse(null);
        if (serverBossBar == null || serverBossBar.isVisible() == visible) {
            return;
        }
        
        serverBossBar.setVisible(visible);
    }
    
    public static boolean isValidGameMode(GameMode gameMode) {
        return gameMode == GameModes.ADVENTURE || gameMode == GameModes.SURVIVAL;
    }
    
    public static Vector3d getRotationVector(Vector3d rotation) {
        return Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
    }
    
    public static float getPercent(int charge, int capacity) {
        if (capacity <= 0 || charge >= capacity) {
            return 1.0F;
        }
        
        return (float) Math.max(0, charge) / capacity;
    }
    
    public static Optional<Integer> getCapacity(Player player) {
        Optional<String> option = Toolbox.getOptionFromSubject(player, Reference.PLUGIN_ID + "-capacity");
        if (option.isPresent()) {
            Optional<Integer> capacity = option.flatMap(Toolbox::parseInteger);
            if (capacity.isPresent()) {
                return capacity;
            }
            
            Jump.getInstance().getLogger().warn("Failed to parse capacity for {} ({})", player.getName(), player.getUniqueId());
        }
        
        return Jump.getInstance().getConfig().map(Config::getDefaultCapacity);
    }
    
    public static Optional<Double> getMultiplier(Player player) {
        Optional<String> option = Toolbox.getOptionFromSubject(player, Reference.PLUGIN_ID + "-multiplier");
        if (option.isPresent()) {
            Optional<Double> multiplier = option.flatMap(Toolbox::parseDouble);
            if (multiplier.isPresent()) {
                return multiplier;
            }
            
            Jump.getInstance().getLogger().warn("Failed to parse multiplier for {} ({})", player.getName(), player.getUniqueId());
        }
        
        return Jump.getInstance().getConfig().map(Config::getDefaultMultiplier);
    }
    
    private static Optional<ServerBossBar> getBossBar(Player player) {
        return Optional.ofNullable(getBossBars().get(player.getUniqueId()));
    }
    
    private static Map<UUID, ServerBossBar> getBossBars() {
        return BOSS_BARS;
    }
}