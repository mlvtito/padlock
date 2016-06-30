/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@RequestScoped
public class PadlockBeanService {
    private PadlockBeanWrapper padlockBeanWrapper = new PadlockBeanWrapper(this);
    
    public void setPadlockBeanWrapper(PadlockBeanWrapper padlockBeanWrapper) {
        this.padlockBeanWrapper = padlockBeanWrapper;
    }

    public PadlockBeanWrapper getPadlockBeanWrapper() {
        return padlockBeanWrapper;
    }
}
