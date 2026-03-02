package tz.go.mnrt.asert.modules.core.services;

import org.springframework.stereotype.Service;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public interface EncryptionService {

    String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    Boolean verify(String data, String signature);

    <T> String getMessageSignature(T payload, Class<?>... classes);

    <T> String getMessageXml(T payload, Class<?>... classes);

    <T> String getPayloadXml(T payload, Class<?>... classes);
}
