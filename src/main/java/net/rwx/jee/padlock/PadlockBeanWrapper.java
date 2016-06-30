/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class PadlockBeanWrapper<P> {
    public final P bean;

    public PadlockBeanWrapper(P bean) {
        this.bean = bean;
    }
}
