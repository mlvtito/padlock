/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.padlock.testapp;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class AuthorizationWithParam {

    private String param;

    public boolean authorized() {
        if (param != null && param.equals("good value")) {
            return true;
        } else {
            return false;
        }
    }

    public void setParam(String param) {
        this.param = param;
    }
}
