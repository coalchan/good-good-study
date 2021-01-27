package com.luckypeng.study.thrift.hs2.util;

import org.apache.thrift.transport.TSaslClientTransport;
import org.apache.thrift.transport.TTransport;

import javax.security.auth.callback.*;
import javax.security.sasl.SaslException;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author coalchan
 * @date 2020/02/20
 */
public class PlainSaslHelper {
    public static TTransport getPlainTransport(String username, String password,
                                               TTransport underlyingTransport) throws SaslException {
        return new TSaslClientTransport("PLAIN", null, "NOSASL", null, new HashMap<String, String>(),
                new PlainCallbackHandler(username, password), underlyingTransport);
    }

    private PlainSaslHelper() {
        throw new UnsupportedOperationException("Can't initialize class");
    }

    public static class PlainCallbackHandler implements CallbackHandler {
        private final String username;
        private final String password;

        public PlainCallbackHandler(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    NameCallback nameCallback = (NameCallback) callback;
                    nameCallback.setName(username);
                } else if (callback instanceof PasswordCallback) {
                    PasswordCallback passCallback = (PasswordCallback) callback;
                    passCallback.setPassword(password.toCharArray());
                } else {
                    throw new UnsupportedCallbackException(callback);
                }
            }
        }
    }
}
