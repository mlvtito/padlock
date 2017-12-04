/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.padlock.testapp;

import javax.ws.rs.ext.Provider;
import net.rwx.jee.padlock.KeyProvider;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@Provider
public class KeyProviderImpl implements KeyProvider {

    private static final String KEY = "mdLhrTztDGE8DepxnTqoedwPgiGm64oQwm4j92Ad"
            + "VNWeiMRiq6PZWvZ8SAGrUuG5xogKkUyH6hcSkrvS4EdwzHnTj2sdshLXwBzyR2qdqCe5b6hTJt"
            + "qyQZALTix7qu3avP98eB946FnNqUWqsGyxmmpqSfxWTkdEPmBnKzaFPaYuQPsyAkFyA5RdAvMc"
            + "wqj8tXHGt7CQ6v83tqk6dNAuKstpGiaYYB65BaPaV8EGJXWTp9ZQrRfn42xS9vGjRU6J";
    
    @Override
    public byte[] getKey() {
        return KEY.getBytes();
    }
    
}
