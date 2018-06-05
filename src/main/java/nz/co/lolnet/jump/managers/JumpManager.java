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

package nz.co.lolnet.jump.managers;

import nz.co.lolnet.jump.Jump;
import nz.co.lolnet.jump.configuration.Config;
import nz.co.lolnet.jump.util.Toolbox;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
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
        if (percent < 0.0F || percent > 1.0F) {
            return;
        }
        
        ServerBossBar serverBossBar = getBossBar(player).orElse(null);
        if (serverBossBar == null) {
            return;
        }
        
        serverBossBar.setVisible(true);
        serverBossBar.setPercent(percent);
        if (serverBossBar.getPercent() >= 1.0F) {
            serverBossBar.setColor(BossBarColors.GREEN);
        } else if (serverBossBar.getPercent() >= 0.5F) {
            serverBossBar.setColor(BossBarColors.YELLOW);
        } else if (serverBossBar.getPercent() >= 0.0F) {
            serverBossBar.setColor(BossBarColors.RED);
        }
    }
    
    public static Optional<ServerBossBar> getBossBar(Player player) {
        return Optional.ofNullable(getBossBars().get(player.getUniqueId()));
    }
    
    private static Map<UUID, ServerBossBar> getBossBars() {
        return BOSS_BARS;
    }
}