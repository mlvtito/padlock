/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.padlock.testapp;

import java.io.Serializable;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class TestUserBean implements Serializable {

    private String name;
    private String mail;

    public TestUserBean() {
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public static class Builder {

        private String name;
        private String mail;

        private Builder() {
        }

        public Builder name(final String value) {
            this.name = value;
            return this;
        }

        public Builder mail(final String value) {
            this.mail = value;
            return this;
        }

        public TestUserBean build() {
            return new TestUserBean(name, mail);
        }
    }

    public static TestUserBean.Builder builder() {
        return new TestUserBean.Builder();
    }

    private TestUserBean(final String name, final String mail) {
        this.name = name;
        this.mail = mail;
    }
}