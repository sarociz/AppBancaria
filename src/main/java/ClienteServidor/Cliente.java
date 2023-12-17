package ClienteServidor;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Cliente {
    public static void conectarCliente(String usuario) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            // Conectar al servidor
            Socket socket = new Socket("localhost", 12345);

            // Generar par de claves
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Enviar clave pública al cliente
            PublicKey publicKey = keyPair.getPublic();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(publicKey);
            //objectOutputStream.flush();

            // Enviar usuario
            ObjectOutputStream usuarioOutputStream = new ObjectOutputStream(socket.getOutputStream());
            usuarioOutputStream.writeObject(usuario);
            //objectoutputStream.flush();

            // Recibir la clave pública del servidor
            ObjectInputStream publicKeyStream = new ObjectInputStream(socket.getInputStream());
            PublicKey servidorpublicKey = (PublicKey) publicKeyStream.readObject();

            System.out.println("Clave publica servidor: " + servidorpublicKey);

//            //Crear y arrancar threads para lectura y escritura
//            Thread readerThread = new Thread(new ClientReader(socket, keyPair.getPrivate()));
//            Thread writerThread = new Thread(new ClientWritter(socket, servidorpublicKey));
//            readerThread.start();
//            writerThread.start();
//
//            // cifrador con la clave pública del servidor
//            Cipher encryptCipher = Cipher.getInstance("RSA");
//            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//
//            byte[] encryptedMessage;
//            // Leer un mensaje
//            String mensaje = br.readLine();
//
//            // Enviar un mensaje cifrado al servidor
//            encryptedMessage = encryptCipher.doFinal(mensaje.getBytes());
//            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//            outputStream.writeObject(encryptedMessage);
//            System.out.println("Mensaje original enviado al servidor: " + mensaje);


            // Cerrar el lector y el socket
//            readerThread.join();
//            writerThread.join();

            socket.close();
            //outputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void iniciarCliente() {
        try {
            // Conectar al servidor
            Socket socket = new Socket("localhost", 12345);

            // Obtener streams de entrada y salida del servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Leer mensajes del servidor
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(message);

                // Aquí puedes implementar la lógica para enviar y recibir mensajes con el servidor

                // Ejemplo básico: enviar mensaje al servidor
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                writer.println(userInput.readLine());
            }

            // Cerrar la conexión con el servidor
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientReader implements Runnable {
        private final Socket socket;
        private PrivateKey privateKey;

        public ClientReader(Socket socket, PrivateKey privateKey) {
            this.socket = socket;
            this.privateKey = privateKey;
        }

        @Override
        public void run() {
            try {

                // Recibir mensaje cifrado desde el cliente
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    byte[] encryptedMessage = (byte[]) objectInputStream.readObject();
                    // Descifrar el mensaje con la clave privada
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
                    System.out.println("Mensaje cifrado: " + new String(encryptedMessage));
                    // Mostrar el mensaje descifrado
                    System.out.println("Mensaje del cliente: " + new String(decryptedMessage));

                }

//                // Cerrar recursos
//                objectOutputStream.close();
//                objectInputStream.close();
//                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientWritter implements Runnable {
        private Socket socket;
        private PublicKey serverPublicKey;

        private ClientWritter(Socket socket, PublicKey serverPublicKey) {
            this.socket = socket;
            this.serverPublicKey = serverPublicKey;
        }


        @Override
        public void run() {
            try {
                ObjectOutputStream objectoutputStream = new ObjectOutputStream(socket.getOutputStream());
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

                while (true) {

                    // Leer mensaje desde la consola y enviar cifrado al servidor
                    System.out.print("Ingrese un mensaje para el servidor: ");
                    String mensaje = consoleReader.readLine();
                    // Descifrar el mensaje con la clave privada
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
                    byte[] encryptedMessage = cipher.doFinal(mensaje.getBytes());
                    objectoutputStream.writeObject(encryptedMessage);
                    objectoutputStream.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
