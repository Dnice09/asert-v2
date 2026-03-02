package tz.go.mnrt.asert.helpers;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

@Slf4j
public class PrivateKeyReader {

    public static PrivateKey get(String filename, String type, String keyPassword, String alias) {
        try {

            KeyStore keyStore = KeyStore.getInstance(type);
            char[] password = keyPassword.toCharArray();
            keyStore.load(new FileInputStream(filename), password);

            return (PrivateKey) keyStore.getKey(alias, password);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidationException(e.getLocalizedMessage());
        }
    }
}
