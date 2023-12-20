package ClienteServidor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Clase con funciones que se encargan del cifrado, descifrado, firma y comprobación de firma
 */
public class FuncionesCifrado {

    public static KeyPair clavesAsimetricas() throws NoSuchAlgorithmException {
        // Generar claves para cifrado asimétrico
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static String firmar(String mensaje, PrivateKey clavePrivada) {
        byte[] firmaBytes;
        try {
            Signature firma = Signature.getInstance("SHA256withRSA");
            firma.initSign(clavePrivada);
            firma.update(mensaje.getBytes("UTF-8"));
            firmaBytes = firma.sign();
        } catch (SignatureException | UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(firmaBytes);
    }

    public static boolean verificarFirma(String mensaje, String firma, PublicKey clavePublica) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(clavePublica);
        signature.update(mensaje.getBytes("UTF-8"));
        byte[] firmaBytes = Base64.getDecoder().decode(firma);
        return signature.verify(firmaBytes);
    }


    public static String cifrar(String mensaje, PublicKey publicKey) {
        byte[] mensajeCifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            mensajeCifrado = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));

            // Codificar los bytes cifrados a Base64
            return Base64.getEncoder().encodeToString(mensajeCifrado);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static String descifrar(String mensajeBase64, PrivateKey privateKey) {
        String mensajeDescifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Decodificar la cadena Base64 a bytes
            byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeBase64);
            mensajeDescifrado = new String(cipher.doFinal(mensajeCifrado), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return mensajeDescifrado;
    }
}
