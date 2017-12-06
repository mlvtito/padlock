/*
 * Copyright 2017 Arnaud Fonce <arnaud.fonce@r-w-x.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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