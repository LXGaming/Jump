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

package nz.co.lolnet.jump.data;

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
    
    public static final Key<Value<Integer>> COUNT_KEY = Key.builder()
            .type(TypeTokens.INTEGER_VALUE_TOKEN)
            .id("count")
            .name("Count")
            .query(DataQuery.of("Count"))
            .build();
    
    private Integer count;
    
    protected JumpData() {
        this(null);
    }
    
    public JumpData(Integer count) {
        this.count = count;
    }
    
    @Override
    public Optional<JumpData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<Integer> count = dataHolder.get(COUNT_KEY);
        if (count.isPresent()) {
            JumpData jumpData = this.copy();
            jumpData.setCount(count.get());
            jumpData = overlap.merge(this, jumpData);
            setCount(jumpData.getCount());
            return Optional.of(this);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<JumpData> from(DataContainer container) {
        Optional<Integer> count = container.getInt(COUNT_KEY.getQuery());
        if (count.isPresent()) {
            setCount(count.get());
            return Optional.of(this);
        }
        
        return Optional.empty();
    }
    
    @Override
    public JumpData copy() {
        return new JumpData(getCount());
    }
    
    @Override
    public JumpImmutableData asImmutable() {
        return new JumpImmutableData(getCount());
    }
    
    @Override
    public int getContentVersion() {
        return 1;
    }
    
    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(COUNT_KEY, this::getCount);
        registerFieldSetter(COUNT_KEY, this::setCount);
        registerKeyValue(COUNT_KEY, this::getCountValue);
    }
    
    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(COUNT_KEY, getCount());
        return dataContainer;
    }
    
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
    
    private Value<Integer> getCountValue() {
        return Sponge.getRegistry().getValueFactory().createValue(COUNT_KEY, getCount());
    }
}