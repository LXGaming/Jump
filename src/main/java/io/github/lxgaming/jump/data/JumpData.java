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

package io.github.lxgaming.jump.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.TypeTokens;

import java.util.Optional;

public class JumpData extends AbstractData<JumpData, JumpImmutableData> {
    
    public static final Key<Value<Integer>> CHARGE_KEY = Key.builder()
            .type(TypeTokens.INTEGER_VALUE_TOKEN)
            .id("charge")
            .name("Charge")
            .query(DataQuery.of("Charge"))
            .build();
    
    private int charge;
    
    protected JumpData() {
        this(0);
    }
    
    public JumpData(int charge) {
        this.charge = charge;
        registerGettersAndSetters();
    }
    
    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(CHARGE_KEY, this::getCharge);
        registerFieldSetter(CHARGE_KEY, this::setCharge);
        registerKeyValue(CHARGE_KEY, this::charge);
    }
    
    @Override
    public Optional<JumpData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<Integer> charge = dataHolder.get(CHARGE_KEY);
        if (charge.isPresent()) {
            JumpData jumpData = this.copy();
            jumpData.setCharge(charge.get());
            jumpData = overlap.merge(this, jumpData);
            setCharge(jumpData.getCharge());
            return Optional.of(this);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<JumpData> from(DataContainer container) {
        Optional<Integer> charge = container.getInt(CHARGE_KEY.getQuery());
        if (charge.isPresent()) {
            setCharge(charge.get());
            return Optional.of(this);
        }
        
        return Optional.empty();
    }
    
    @Override
    public JumpData copy() {
        return new JumpData(getCharge());
    }
    
    @Override
    public JumpImmutableData asImmutable() {
        return new JumpImmutableData(getCharge());
    }
    
    @Override
    public int getContentVersion() {
        return 1;
    }
    
    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(CHARGE_KEY, getCharge());
        return dataContainer;
    }
    
    public int getCharge() {
        return charge;
    }
    
    public void setCharge(int charge) {
        this.charge = charge;
    }
    
    private Value<Integer> charge() {
        return Sponge.getRegistry().getValueFactory().createValue(CHARGE_KEY, getCharge());
    }
}