package tz.go.mnrt.asert.helpers;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;

@Slf4j
public class PublicKeyReader {


    public static PublicKey get(String filename, String type, String keyPassword, String alias) {
        try {

            KeyStore keyStore = KeyStore.getInstance(type);
            char[] password = keyPassword.toCharArray();
            keyStore.load(new FileInputStream(filename), password);

            return keyStore.getCertificate(alias).getPublicKey();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidationException(e.getLocalizedMessage());
        }
    }
}
