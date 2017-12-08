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
package net.rwx.jee.padlock;

import java.io.Serializable;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
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
