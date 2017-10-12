/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.io.Serializable;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class TestSessionBean implements Serializable {

    private String login;
    private String fullName;

    public String getLogin() {
        return login;
    }

    public String getFullName() {
        return fullName;
    }

    public static class Builder {

        private String login;
        private String fullName;

        private Builder() {
        }

        public Builder login(final String value) {
            this.login = value;
            return this;
        }

        public Builder fullName(final String value) {
            this.fullName = value;
            return this;
        }

        public TestSessionBean build() {
            return new net.rwx.jee.padlock.TestSessionBean(login, fullName);
        }
    }

    public static TestSessionBean.Builder builder() {
        return new TestSessionBean.Builder();
    }

    private TestSessionBean(final String login, final String fullName) {
        this.login = login;
        this.fullName = fullName;
    }
}
