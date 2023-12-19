package ClienteServidor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class FuncionesCifrado {

    public static KeyPair generarParDeClaves() throws NoSuchAlgorithmException {
        // Generar par de claves para cifrado asimétrico (RSA)
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
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

    public static String codificarClavePublica(PublicKey clave) {
        return Base64.getEncoder().encodeToString(clave.getEncoded());
    }

    public static PublicKey decodificarClavePublica(String claveCodificada) throws Exception {
        byte[] claveBytes = Base64.getDecoder().decode(claveCodificada);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(claveBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static String cifrar(String mensaje, PublicKey publicKey) {
        // Leer mensaje y enviarlo cifrado
        byte[] mensajeCifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            mensajeCifrado = cipher.doFinal(mensaje.getBytes());

            return Arrays.toString(mensajeCifrado);

        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static String descifrar(String mensajeBase64, PrivateKey privateKey) {
        String mensajeDescifrado = null;
        try {
            // Leer y descifrar mensajes del cliente
            byte[] mensajeCifrado = mensajeBase64.getBytes();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            mensajeDescifrado = new String(cipher.doFinal(mensajeCifrado));
            System.out.println("Cliente dice: " + mensajeDescifrado);

        } catch (NoSuchPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return mensajeDescifrado;
    }
}
