package ClienteServidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import javax.crypto.Cipher;

public class Servidor {

    public static void main(String[] args) {
        try {
            // Configurar el servidor para escuchar en el puerto 12345
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Servidor esperando conexiones...");

            while (true) {
                // Aceptar la conexión del cliente
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado");

                // Crear un nuevo hilo para manejar la conexión con el cliente
                Thread clientThread = new Thread(new ClientHandler(socket));
                clientThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Generar par de claves
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                // Enviar clave pública al cliente
                PublicKey publicKey = keyPair.getPublic();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(publicKey);

                // Recibir mensaje cifrado desde el cliente
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                byte[] encryptedMessage = (byte[]) objectInputStream.readObject();
                System.out.println("Mensaje cifrado: " + new String(encryptedMessage));

                // Descifrar el mensaje con la clave privada
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                byte[] decryptedMessage = cipher.doFinal(encryptedMessage);

                // Mostrar el mensaje descifrado
                System.out.println("Mensaje del cliente: " + new String(decryptedMessage));

                // Cerrar recursos
                objectOutputStream.close();
                objectInputStream.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

