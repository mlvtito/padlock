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
public class TestAuthorizedWithParameter {
    private Integer firstParameter;
    private Integer secondParameter;
    private Integer myParameter;
    
    public boolean authorized() {
        return true;
    }

    public Integer getFirstParameter() {
        return firstParameter;
    }

    public void setFirstParameter(Integer firstParameter) {
        this.firstParameter = firstParameter;
    }

    public Integer getSecondParameter() {
        return secondParameter;
    }

    public void setSecondParameter(Integer secondParameter) {
        this.secondParameter = secondParameter;
    }

    public Integer getMyParameter() {
        return myParameter;
    }

    public void setMyParameter(Integer pathParameter) {
        this.myParameter = pathParameter;
    }
}
