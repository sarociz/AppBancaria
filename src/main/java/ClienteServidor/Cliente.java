package ClienteServidor;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

public class Cliente {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                // Conectar al servidor
                Socket socket = new Socket("localhost", 12345);

                // Recibir la clave pública del servidor
                ObjectInputStream publicKeyStream = new ObjectInputStream(socket.getInputStream());
                PublicKey publicKey = (PublicKey) publicKeyStream.readObject();

                // cifrador con la clave pública del servidor
                Cipher encryptCipher = Cipher.getInstance("RSA");
                encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);


                byte[] encryptedMessage;
                // Leer un mensaje desde la consola
                System.out.print("Ingrese un mensaje para enviar al servidor (o escriba 'salir' para cerrar): ");
                String mensaje = br.readLine();

                // Enviar un mensaje cifrado al servidor
                encryptedMessage = encryptCipher.doFinal(mensaje.getBytes());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(encryptedMessage);
                System.out.println("Mensaje original enviado al servidor: " + mensaje);



                // Cerrar el lector y el socket

                socket.close();
                outputStream.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
