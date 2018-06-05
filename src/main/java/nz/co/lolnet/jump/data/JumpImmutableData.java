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
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class JumpImmutableData extends AbstractImmutableData<JumpImmutableData, JumpData> {
    
    private final Integer count;
    
    protected JumpImmutableData(Integer count) {
        this.count = count;
    }
    
    @Override
    public JumpData asMutable() {
        return new JumpData(getCount());
    }
    
    @Override
    public int getContentVersion() {
        return 1;
    }
    
    @Override
    protected void registerGetters() {
        registerFieldGetter(JumpData.COUNT_KEY, this::getCount);
        registerKeyValue(JumpData.COUNT_KEY, this::getCountValue);
    }
    
    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(JumpData.COUNT_KEY, getCount());
        return dataContainer;
    }
    
    public Integer getCount() {
        return count;
    }
    
    private ImmutableValue<Integer> getCountValue() {
        return Sponge.getRegistry().getValueFactory().createValue(JumpData.COUNT_KEY, getCount()).asImmutable();
    }
}