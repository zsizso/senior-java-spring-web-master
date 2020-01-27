package hu.ponte.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

@Service
public class SignService {

    @Autowired
    ResourceLoader resourceLoader;


    public  byte[] getPrivateKeyBytes(String filename) throws Exception {
        Resource resource = resourceLoader.getResource(
                "classpath:config.keys/key.private");
        byte[] f = Files.readAllBytes(resource.getFile().toPath());
        byte[] messageBytes = Files.readAllBytes(Paths.get(filename));

        PrivateKey privateKey = getPrivate(f);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(messageBytes);
        byte[] digitalSignature = signature.sign();

    }

    public static PrivateKey getPrivate(byte[] privateKeyBytes)
            throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
