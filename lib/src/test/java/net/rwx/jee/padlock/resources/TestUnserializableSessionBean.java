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
package net.rwx.jee.padlock.resources;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class TestUnserializableSessionBean {

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

        public TestUnserializableSessionBean build() {
            return new net.rwx.jee.padlock.resources.TestUnserializableSessionBean(login, fullName);
        }
    }

    public static TestUnserializableSessionBean.Builder builder() {
        return new TestUnserializableSessionBean.Builder();
    }

    private TestUnserializableSessionBean(final String login, final String fullName) {
        this.login = login;
        this.fullName = fullName;
    }
}
