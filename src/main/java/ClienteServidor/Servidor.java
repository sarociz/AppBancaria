package ClienteServidor;

import models.Usuario;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;

public class Servidor {
    private static Map<String, Usuario> usuarios = new HashMap<>();

    public static void main(String[] args) {
        startServidor();
    }

    public static void startServidor() {
        try {
            // Configurar el servidor para escuchar en el puerto 12345
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Servidor esperando conexiones...");

            // Generar par de claves
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Aceptar la conexión del cliente
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado");

            // Enviar clave pública al cliente
            PublicKey publicKey = keyPair.getPublic();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(publicKey);
            objectOutputStream.flush();

            System.out.println("Clave privada servidor: " + keyPair.getPrivate());
            System.out.println("Clave publica servidor: " + keyPair.getPublic());


            // Recibir clave pública del cliente
            ObjectInputStream clientPublicKeyStream = new ObjectInputStream(socket.getInputStream());
            PublicKey clientPublicKey = (PublicKey) clientPublicKeyStream.readObject();

            System.out.println("Clave publica cliente: " + clientPublicKey);

            // Recibir nombre de usuario
            ObjectInputStream usuarioPublicStream = new ObjectInputStream(socket.getInputStream());
            String usuario = (String) usuarioPublicStream.readObject();

            System.out.println("¡Bienvenido, " + usuario + "!" );
//
//            // Crear hilos para manejar la conexión con el cliente
//            Thread clientReaderThread = new Thread(new ServerReader(socket, keyPair.getPrivate()));
//            Thread clientWritterThread = new Thread(new ServerWritter(socket, clientPublicKey));
//            clientReaderThread.start();
//            clientWritterThread.start();
//
//             //Esperar a que los threads terminen
//            clientReaderThread.join();
//            clientWritterThread.join();
//
            socket.close();
            serverSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void conectarConCliente(String nombreUsuario, int userId) {
        // Aquí puedes agregar lógica para aceptar conexiones de clientes
        // y comunicarte con el cliente después de la autenticación exitosa
        // Por ejemplo, podrías iniciar un nuevo hilo o ejecutar una tarea en segundo plano.

        // Ejemplo: Iniciar un hilo para manejar la conexión
        Thread connectionThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(12345);
                Socket clientSocket = serverSocket.accept();

                // Recibir clave pública del cliente
                ObjectInputStream clientPublicKeyStream = new ObjectInputStream(clientSocket.getInputStream());
                PublicKey clientPublicKey = (PublicKey) clientPublicKeyStream.readObject();

                System.out.println("Clave publica cliente: " + clientPublicKey);

                // Aquí puedes enviar información adicional al cliente si es necesario
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println("¡Bienvenido, " + nombreUsuario + "! Tu ID es " + userId);

                // Implementa la lógica de comunicación con el cliente según tus necesidades

                // Cierra la conexión con el cliente cuando sea apropiado
                clientSocket.close();
                serverSocket.close();

            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        connectionThread.start();
    }

    private static class ServerReader implements Runnable {
        private final Socket socket;
        private PrivateKey privateKey;

        public ServerReader(Socket socket, PrivateKey privateKey) {
            this.socket = socket;
            this.privateKey = privateKey;

        }

        @Override
        public void run() {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    // Recibir mensaje cifrado desde el cliente
                    byte[] encryptedMessage = (byte[]) objectInputStream.readObject();
                    System.out.println("Mensaje cifrado: " + new String(encryptedMessage));

                    // Descifrar el mensaje con la clave privada
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    byte[] decryptedMessage = cipher.doFinal(encryptedMessage);

                    // Mostrar el mensaje descifrado
                    System.out.println("Mensaje del cliente: " + new String(decryptedMessage));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ServerWritter implements Runnable {
        private Socket socket;
        private PublicKey clientPublicKey;

        private ServerWritter(Socket socket, PublicKey clientPublicKey) {
            this.socket = socket;
            this.clientPublicKey = clientPublicKey;
        }


        @Override
        public void run() {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    // Leer mensaje desde la consola y enviar cifrado al cliente
                    System.out.print("Ingrese un mensaje para el cliente: ");
                    String mensaje = consoleReader.readLine();
                    // Descifrar el mensaje con la clave privada
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
                    byte[] encryptedMessage = cipher.doFinal(mensaje.getBytes());
                    outputStream.writeObject(encryptedMessage);
                    outputStream.flush();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

