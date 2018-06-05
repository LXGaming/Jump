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

package nz.co.lolnet.jump.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Config {
    
    @Setting(value = "boss-bar-title", comment = "Boss bar title")
    private String bossBarTitle = "&6\u26A1 &9&lJump Charge &r&6\u26A1";
    
    @Setting(value = "debug", comment = "For debugging purposes")
    private boolean debug = false;
    
    @Setting(value = "default-jump-level", comment = "Default jump level")
    private double defaultJumpLevel = 1.5D;
    
    @Setting(value = "void-jump-level", comment = "Void jump level, used when a player falls into the void")
    private double voidJumpLevel = 10.0D;
    
    public String getBossBarTitle() {
        return bossBarTitle;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
    public double getDefaultJumpLevel() {
        return defaultJumpLevel;
    }
    
    public double getVoidJumpLevel() {
        return voidJumpLevel;
    }
}