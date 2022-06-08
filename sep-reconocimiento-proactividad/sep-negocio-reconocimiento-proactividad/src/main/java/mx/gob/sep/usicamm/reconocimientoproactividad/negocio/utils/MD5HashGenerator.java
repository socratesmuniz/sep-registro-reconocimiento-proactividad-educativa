package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author albertosanchezlopez
 */
public class MD5HashGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(MD5HashGenerator.class);
    
    public static String getMD5Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(Constantes.MD5_ALGORITHM);
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            
            return bytesToHex(hash); // make it printable
        }
        catch(NoSuchAlgorithmException ex) {
            LOG.warn("Hash de: [{}] - no fue posible: {}", data, ex.getLocalizedMessage());
        }
        return Constantes.NO_TOKEN;
    }

    private static String  bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }
}
