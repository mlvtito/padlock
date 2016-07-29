/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.util.Arrays;
import java.util.Collection;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
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
}
