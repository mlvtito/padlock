/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class PadlockBeanProducer {
 
    @Inject
    private PadlockBeanService padlockBeanService;
    
    @Produces @PadlockBean
    <B> PadlockBeanWrapper<B> producePadlockBean(InjectionPoint ip) {
        return padlockBeanService.getPadlockBeanWrapper();
    }

}
