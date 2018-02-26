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
package net.rwx.padlock;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Helper;
import javax.enterprise.context.ApplicationScoped;

/**
 * This service provide method to hash and verify passwords.
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
@ApplicationScoped
public class PasswordService {
    
    private final static Argon2 ARGON2 = Argon2Factory.create();
    private final static int NB_ITERATIONS = Argon2Helper.findIterations(ARGON2, 1000, 65536, 1);
    
    public String hash(char[] password, boolean wipePassword) {
        try {
            return ARGON2.hash(1, 65536, 1, password);
        } finally {
            if (wipePassword) {
                ARGON2.wipeArray(password);
            }
        }
    }

    public boolean verify(char[] password, String hash, boolean wipePassword) {
        try {
            return ARGON2.verify(hash, password);
        } finally {
            if (wipePassword) {
                ARGON2.wipeArray(password);
            }
        }
    }
}
