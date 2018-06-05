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

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class JumpDataBuilder extends AbstractDataBuilder<JumpData> implements DataManipulatorBuilder<JumpData, JumpImmutableData> {
    
    public JumpDataBuilder() {
        super(JumpData.class, 1);
    }
    
    public static JumpDataBuilder builder() {
        return new JumpDataBuilder();
    }
    
    @Override
    public JumpData create() {
        return new JumpData();
    }
    
    @Override
    public Optional<JumpData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }
    
    @Override
    protected Optional<JumpData> buildContent(DataView dataView) throws InvalidDataException {
        return create().from(dataView.copy());
    }
}