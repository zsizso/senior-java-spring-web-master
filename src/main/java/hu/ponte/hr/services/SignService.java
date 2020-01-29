package hu.ponte.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class SignService {

    @Autowired
    private ResourceLoader resourceLoader;

    public  String getSigned(byte[] theFile) {
        try {
        File keyFile  = new ClassPathResource("config/keys/key.private").getFile();
        byte[] pKey = Files.readAllBytes(keyFile.toPath());

        PrivateKey privateKey = getPrivate(pKey);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(theFile);

        byte[] digitalSignature = signature.sign();
       return  Base64.getEncoder().encodeToString(digitalSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private PrivateKey getPrivate(byte[] privateKeyBytes)
            throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
