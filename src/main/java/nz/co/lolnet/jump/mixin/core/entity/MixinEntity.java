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

package nz.co.lolnet.jump.mixin.core.entity;

import net.minecraft.entity.Entity;
import nz.co.lolnet.jump.event.ChangeFlyEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 1337)
public abstract class MixinEntity {
    
    @Shadow
    public abstract boolean getFlag(int flag);
    
    @Inject(method = "setFlag", at = @At(value = "HEAD"), cancellable = true)
    private void onSetFlag(int flag, boolean set, CallbackInfo callbackInfo) {
        if (this instanceof Player && flag == 7 && set != getFlag(flag)) {
            Player player = (Player) this;
            Sponge.getCauseStackManager().pushCause(player);
            if (Sponge.getEventManager().post(new ChangeFlyEvent(player, set, true, Sponge.getCauseStackManager().getCurrentCause()))) {
                callbackInfo.cancel();
            }
            
            Sponge.getCauseStackManager().popCause();
        }
    }
}