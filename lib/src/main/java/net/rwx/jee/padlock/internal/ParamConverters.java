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
package net.rwx.jee.padlock.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ParamConverter;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
@ApplicationScoped
class ParamConverters {

    private Map<String, ParamConverter> converters;

    public ParamConverters() {
        converters = new HashMap<>();
        converters.put(int.class.getName(), new IntegerConverter());
        converters.put(long.class.getName(), new LongConverter());
        converters.put(boolean.class.getName(), new BooleanConverter());
        converters.put(Integer.class.getName(), new IntegerConverter());
        converters.put(Long.class.getName(), new LongConverter());
        converters.put(Boolean.class.getName(), new BooleanConverter());
        converters.put(String.class.getName(), new StringConverter());
    }

    public <T> T convertValueToType(String value, Class<T> type, Collection<Class<?>> customConverters) {
        ParamConverter<T> converter = findParamConverter(type);
        return converter.fromString(value);
    }

    private <T> ParamConverter<T> findParamConverter(Class<T> type) {
        return converters.get(type.getName());
    }

    private abstract class FromStringConverter<T> implements ParamConverter<T> {

        @Override
        public String toString(T value) {
            throw new UnsupportedOperationException("Should convert to string with this converter");
        }
    }

    private class IntegerConverter extends FromStringConverter<Integer> {

        @Override
        public Integer fromString(String value) {
            return Integer.parseInt(value);
        }
    }

    private class LongConverter extends FromStringConverter<Long> {

        @Override
        public Long fromString(String value) {
            return Long.parseLong(value);
        }
    }
    
    private class StringConverter extends FromStringConverter<String> {

        @Override
        public String fromString(String value) {
            return value;
        }
    }
    
    private class BooleanConverter extends FromStringConverter<Boolean> {

        @Override
        public Boolean fromString(String value) {
            return Boolean.parseBoolean(value);
        }
    }
}
