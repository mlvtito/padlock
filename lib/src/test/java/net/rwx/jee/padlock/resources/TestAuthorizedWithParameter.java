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
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
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
