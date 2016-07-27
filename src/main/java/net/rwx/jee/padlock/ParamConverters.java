/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ParamConverter;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@ApplicationScoped
class ParamConverters {

    private Map<String, ParamConverter> converters;

    public ParamConverters() {
        converters = new HashMap<>();
        converters.put(Integer.class.getName(), new IntegerConverter());
        converters.put(int.class.getName(), new IntegerConverter());
        converters.put(Long.class.getName(), new LongConverter());
        converters.put(long.class.getName(), new LongConverter());
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
}
