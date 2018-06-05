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

package nz.co.lolnet.jump.mixin.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import nz.co.lolnet.jump.event.ChangeFlyEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NetHandlerPlayServer.class, priority = 1337)
public abstract class MixinNetHandlerPlayServer {
    
    @Shadow
    public EntityPlayerMP player;
    
    @Redirect(method = "processPlayerAbilities", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerCapabilities;isFlying:Z", opcode = Opcodes.PUTFIELD))
    public void onProcessPlayerAbilities(PlayerCapabilities playerCapabilities, boolean isFlying) {
        if (playerCapabilities.isFlying != isFlying) {
            Sponge.getCauseStackManager().pushCause(this.player);
            if (Sponge.getEventManager().post(new ChangeFlyEvent((Player) this.player, isFlying, Sponge.getCauseStackManager().getCurrentCause()))) {
                player.connection.sendPacket(new SPacketPlayerAbilities(playerCapabilities));
            } else {
                playerCapabilities.isFlying = isFlying;
            }
            
            Sponge.getCauseStackManager().popCause();
        }
    }
}