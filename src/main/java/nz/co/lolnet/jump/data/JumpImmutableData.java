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
    
    private final int charge;
    
    protected JumpImmutableData(int charge) {
        this.charge = charge;
        registerGetters();
    }
    
    @Override
    protected void registerGetters() {
        registerFieldGetter(JumpData.CHARGE_KEY, this::getCharge);
        registerKeyValue(JumpData.CHARGE_KEY, this::charge);
    }
    
    @Override
    public JumpData asMutable() {
        return new JumpData(getCharge());
    }
    
    @Override
    public int getContentVersion() {
        return 1;
    }
    
    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(JumpData.CHARGE_KEY, getCharge());
        return dataContainer;
    }
    
    public int getCharge() {
        return charge;
    }
    
    private ImmutableValue<Integer> charge() {
        return Sponge.getRegistry().getValueFactory().createValue(JumpData.CHARGE_KEY, getCharge()).asImmutable();
    }
}