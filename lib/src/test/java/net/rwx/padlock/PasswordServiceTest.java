/*
 * Copyright 2018 Arnaud Fonce <arnaud.fonce@r-w-x.net>.
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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
public class PasswordServiceTest {
    
    private static final String ARGON_HASH = "$argon2i$v=19$m=65535,t=1,p=1$sDN2qW8B2lL3z1OZ2oO8Qw$79ONmOxnUBZM7f8RHng"
            + "+lxKszQSASMRkBtr3wcPqzRc";
    
    private static final boolean WIPE_PWD = true;
    
    private final PasswordService passwordService = new PasswordService();

    @Test
    public void should_HaveGoodHash_while_Hashing() {
        String hash = passwordService.hash("bad-password".toCharArray(), WIPE_PWD);
        assertThat(hash).startsWith("$argon2");
    }

    @Test
    public void should_HaveGoodPassword_while_Verifying() {
        boolean verify = passwordService.verify("test-password".toCharArray(), ARGON_HASH, WIPE_PWD);
        assertThat(verify).isTrue();
    }
    
    @Test
    public void should_HaveWrongPassword_while_Verifying() {
        boolean verify = passwordService.verify("bad-password".toCharArray(), ARGON_HASH, WIPE_PWD);
        assertThat(verify).isFalse();
    }
    
    @Test
    public void should_WipePasswordData_while_Verifying() {
        char[] pwd = "bad-password".toCharArray();
        passwordService.verify(pwd, ARGON_HASH, WIPE_PWD);
        for(int i=0; i < pwd[i]; i++){assertThat(pwd[i]).isEqualTo(0);}
    }
    
    @Test
    public void should_NotWipePasswordData_while_Verifying() {
        char[] pwd = "bad-password".toCharArray();
        passwordService.verify(pwd, ARGON_HASH, !WIPE_PWD);
        assertThat(new String(pwd)).isEqualTo("bad-password");
    }
    
    @Test
    public void should_WipePasswordData_while_Hashing() {
        char[] pwd = "bad-password".toCharArray();
        passwordService.hash(pwd, WIPE_PWD);
        for(int i=0; i < pwd[i]; i++){assertThat(pwd[i]).isEqualTo(0);}
    }
    
    @Test
    public void should_NotWipePasswordData_while_Hashing() {
        char[] pwd = "bad-password".toCharArray();
        passwordService.hash(pwd, !WIPE_PWD);
        assertThat(new String(pwd)).isEqualTo("bad-password");
    }
}
