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

package io.github.lxgaming.jump.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Config {
    
    @Setting(value = "boss-bar-title", comment = "Boss bar title")
    private String bossBarTitle = "&r&6\u26A1 &9&lJump Charge &r&6\u26A1";
    
    @Setting(value = "debug", comment = "For debugging purposes")
    private boolean debug = false;
    
    @Setting(value = "default-multiplier", comment = "Default multiplier")
    private double defaultMultiplier = 1.5D;
    
    @Setting(value = "default-capacity", comment = "Default capacity")
    private int defaultCapacity = 100;
    
    @Setting(value = "void-multiplier", comment = "Void multiplier, Applied to the player when below Y 0")
    private double voidMultiplier = 10.0D;
    
    public String getBossBarTitle() {
        return bossBarTitle;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
    public double getDefaultMultiplier() {
        return defaultMultiplier;
    }
    
    public int getDefaultCapacity() {
        return defaultCapacity;
    }
    
    public double getVoidMultiplier() {
        return voidMultiplier;
    }
}