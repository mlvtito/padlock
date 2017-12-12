/*
 * Copyright 2017 Arnaud Fonce <arnaud.fonce@r-w-x.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rwx.padlock.internal;

import net.rwx.padlock.internal.ParamConverters;
import java.util.Arrays;
import java.util.Collection;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
public class ParamConvertersTest {
    private static final Collection<Class<?>> NO_ADDITIONAL_CONVERTER = Arrays.asList();
    
    private ParamConverters paramConverters = new ParamConverters();
    
    @Test
    public void should_GetInteger_when_Converting() {
        Integer value = paramConverters.convertValueToType("65", Integer.class, NO_ADDITIONAL_CONVERTER);
        assertThat(value).isEqualTo(65);
    }
    
    @Test
    public void should_GetIntegerNative_when_Converting() {
        int value = paramConverters.convertValueToType("65", int.class, NO_ADDITIONAL_CONVERTER);
        assertThat(value).isEqualTo(65);
    }
    
    @Test
    public void should_GetLong_when_Converting() {
        Long value = paramConverters.convertValueToType("65", Long.class, NO_ADDITIONAL_CONVERTER);
        assertThat(value).isEqualTo(65L);
    }
    
    @Test
    public void should_GetLongNative_when_Converting() {
        long value = paramConverters.convertValueToType("65", long.class, NO_ADDITIONAL_CONVERTER);
        assertThat(value).isEqualTo(65L);
    }
    
    @Test
    public void should_GetString_when_Converting() {
        String value = paramConverters.convertValueToType("my string", String.class, NO_ADDITIONAL_CONVERTER);
        assertThat(value).isEqualTo("my string");
    }
    
    @Test
    public void should_GetBoolean_when_Converting() {
        Boolean value = paramConverters.convertValueToType("true", Boolean.class, NO_ADDITIONAL_CONVERTER);
        assertThat(value).isTrue();
    }
    
    @Test
    public void should_GetBooleanNative_when_Converting() {
        boolean value = paramConverters.convertValueToType("false", boolean.class, NO_ADDITIONAL_CONVERTER);
        assertThat(value).isFalse();
    }
}
