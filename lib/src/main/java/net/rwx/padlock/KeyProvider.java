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

import javax.ws.rs.ext.Provider;

/**
 * Create a class that inherit from {@link KeyProvider} to define the key used to encrypt session token.
 *
 * <p>
 * The created class must be annoted with {@link Provider}.</p>
 *
 * <p>
 * For instance :
 * 
 * <pre>
 * &#64;Provider
 * public class KeyProviderImpl implements KeyProvider {
 *
 *   &#64;Override
 *   public byte[] getKey() {
 *       return "My Key...".getBytes();
 *   }
 * }
 * </pre>
 *
 * @see Provider
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
public interface KeyProvider {

    byte[] getKey();
}
