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
public class PadlockBeanWrapper {

    private Object bean;
    
    public <P> P getBean(Class<P> beanType) {
        return (P)bean;
    }
    
    void setBean(Object bean) {
        this.bean = bean;
    }
}
