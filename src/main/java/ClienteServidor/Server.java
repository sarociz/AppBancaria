package ClienteServidor;

import dao.CuentaBancariaImpl;
import dao.TransferenciaDaoImpl;
import dao.UsuarioDaoImpl;
import models.CuentaBancaria;
import models.Transferencia;
import models.Usuario;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

import static ClienteServidor.FuncionesCifrado.*;

/**
 * Clase servidor donde se ejecuta el servidor y se crea un thread para manejar al cliente
 */
public class Server {
    private static final KeyPair keypair;

    static {
        try {
            keypair = clavesAsimetricas();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static Usuario usuario;
    private static Socket clientSocket;

    public static void main(String[] args) {
        try {
            // Configurar el servidor para escuchar en el puerto 12345
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Servidor esperando conexiones...");


            while (true) {
                UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
                CuentaBancariaImpl cuentaBancariaDao = new CuentaBancariaImpl();
                Usuario usuario;
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado");

                // Manejar la comunicación con el cliente en un hilo separado
                Thread clientHandlerThread = new Thread(new ClienteHandler(socket, keypair.getPrivate(), keypair.getPublic(), cuentaBancariaDao, usuarioDao));
                clientHandlerThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * La clase clientehandler se encarga de la comunicación entre cliente y servidor, recibe los mensajes del cliente y
     * responde de forma adecuada.
     */
    public static class ClienteHandler implements Runnable {
        private final Socket socket;
        private ObjectOutputStream objectOutputStream;
        private final ObjectInputStream objectInputStream;
        private final PrivateKey privateKey;
        public PublicKey publicKey;
        private CuentaBancariaImpl cuentaBancariaDao;
        private TransferenciaDaoImpl transferenciaDao = new TransferenciaDaoImpl();
        private UsuarioDaoImpl usuarioDao;
        // Constants for choices
        private static final int LOGIN = 1;
        private static final int REGISTER = 2;
        private static final int EXIT = 3;
        private static final int NUEVACUENTA = 4;
        private static final int TRANFERENCIACC = 5;
        private static final int TRANSFERENCIAD = 6;

        private boolean logeado = false;
        PublicKey clientPublicKey;

        public ClienteHandler(Socket socket, PrivateKey privateKey, PublicKey publicKey, CuentaBancariaImpl cuentaBancariaDao, UsuarioDaoImpl usuarioDao) throws IOException {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.cuentaBancariaDao = cuentaBancariaDao;
            this.usuarioDao = usuarioDao;
        }

        @Override
        public void run() {
            try {
                // Enviar clave pública al cliente
                objectOutputStream.writeObject(publicKey);
                //publicKeyStream.flush();

                //recoger clave pública del cliente
                clientPublicKey = (PublicKey) objectInputStream.readObject();
                System.out.println(clientPublicKey);

                boolean userExists = false;

                while (!logeado) {
                    int botonDOCUMENTO = -1;
                    RegistroLogin(userExists, botonDOCUMENTO);
                }
                boolean menu = (boolean) objectInputStream.readObject();
                if (menu) {
                    try {
                        while (true) {
                            try {
                                int choice = (int) objectInputStream.readObject();
                                switch (choice) {
                                    case NUEVACUENTA:
                                        Usuario usuario1 = (Usuario) objectInputStream.readObject();
                                        String tipo = (String) objectInputStream.readObject();
                                        crearCuenta(usuario, tipo);
                                        break;
                                    case TRANFERENCIACC:
                                        String numcuenta = (String) objectInputStream.readObject();
                                        String cuentadestino = (String) objectInputStream.readObject();
                                        Double valor = (Double) objectInputStream.readObject();
                                        tranferenciacc(numcuenta, cuentadestino, valor);
                                        break;
                                    case TRANSFERENCIAD:
                                        String numcuenta2 = (String) objectInputStream.readObject();
                                        String cuentadestino2 = (String) objectInputStream.readObject();
                                        Double valor2 = (Double) objectInputStream.readObject();
                                        String nombre = (String) objectInputStream.readObject();
                                        String apellidos = (String) objectInputStream.readObject();
                                        tranferenciaDir(numcuenta2, cuentadestino2, valor2, nombre, apellidos);
                                        break;
                                    case EXIT:
                                        System.out.println("saliendo");
                                        break;
                                    default:
                                        System.out.println("Opción no válida");
                                }


                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {
                        try {
                            objectInputStream.close();
                            objectOutputStream.close();
                        } catch (IOException ex) {
                            e.printStackTrace();
                        }
                    }
                }


            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    objectInputStream.close();
                    objectOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * En esta funcion se encarga de verificar el login del usuario(si existe, comprobar contraseña, etc..)
         * y del registro de los usuarios
         *
         * @param userExists se encarga de comunicar si el usuario existe en la base de datos o no
         * @throws IOException            controla las excepciones al leer los mensajes del cliente
         * @throws ClassNotFoundException controla las excepciones al leer los mensajes del cliente
         */
        private void RegistroLogin(boolean userExists, int botonDocumento) throws IOException, ClassNotFoundException {
            int choice = (int) objectInputStream.readObject();
            switch (choice) {
                case LOGIN:
                    Object datosLogin = objectInputStream.readObject();
                    Usuario usuariologin = (Usuario) datosLogin;
                    Usuario datosCompletos = null;
                    List<CuentaBancaria> cuentaBancariaList = new ArrayList<>();

                    String nomusu = usuariologin.getUsuario();
                    String contrasena = usuariologin.getContrasena();

                    String hashContrasena = calcularHash(contrasena);

                    if (usuarioDao.existeUsuario(nomusu)) {
                        // busco el usuario en la base de datos
                        usuario = usuarioDao.findByUsuario(nomusu);
                        // Check if the username exists and the password matches
                        if (usuario.getContrasena() != null && usuario.getContrasena().equals(hashContrasena)) {
                            System.out.println("¡Credenciales correctas!");
                            cuentaBancariaList = cuentaBancariaDao.findByUsuarioId(usuario.getId());
                            System.out.println(cuentaBancariaList);
                            userExists = true;
                        } else {
                            System.out.println("ERROR. Verifique sus credenciales.");
                            userExists = false;
                        }
                    } else {
                        System.out.println("Usuario no encontrado");
                        userExists = false;
                    }

                    objectOutputStream.writeObject(userExists);// enviar si existe o no
                    objectOutputStream.writeObject(usuario); //enviar todos los datos del usuario
                    objectOutputStream.writeObject(cuentaBancariaList); // enviar las cuentas bancarias del usuario

                    if (userExists) {
                        logeado = true;
                    } else {
                        System.out.println("Usuario no existe.");

                    }

                    //}
                    break;
                case REGISTER: //boton registro
                    //envio del documento a firmar
                    DocumentoCondiciones();

                    // Espera la respuesta del cliente
                    boolean documentoAceptado = (boolean) objectInputStream.readObject();
                    // Si lo ha aceptado
                    if (documentoAceptado) {

                        // Recibir datos del cliente para el registro
                        Usuario nuevoUsuario = (Usuario) objectInputStream.readObject();

                        // Realizar el registro del usuario
                        hashContrasena = calcularHash(nuevoUsuario.getContrasena());

                        if (usuarioDao.existeUsuario(nuevoUsuario.getUsuario())) {
                            System.out.println("Nombre de usuario en uso. Por favor, elija otro.");
                            userExists = false;
                        } else {
                            nuevoUsuario.setContrasena(hashContrasena);
                            usuarioDao.create(nuevoUsuario);
                            System.out.println("Registro exitoso");
                            userExists = true;
                        }
                        List<CuentaBancaria> cuentaBancariaList2 = new ArrayList<>();

                        objectOutputStream.writeObject(userExists);// enviar si existe o no
                        objectOutputStream.writeObject(nuevoUsuario); //enviar todos los datos del usuario
                        objectOutputStream.writeObject(cuentaBancariaList2); // enviar las cuentas bancarias del usuario

                        if (userExists) {
                            logeado = true;
                        } else {
                            System.out.println("Usuario no existe.");

                        }

                    } else {
                        System.out.println("Usuario no aceptó el documento.");
                        objectOutputStream.writeObject("No ha aceptado el documento. Volviendo al inicio...");
                        break;
                    }
                    break;
                case EXIT:
                    System.out.println("Usuario ha salido.");
                    return;
                default:
                    // Handle unexpected choices
                    break;
            }
        }

        /**
         * función para cifrar la contraseña
         *
         * @param contrasena contraseña sin seguridad
         * @return devuelve la contraseña codificada para que sea segura
         */
        public static String calcularHash(String contrasena) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = md.digest(contrasena.getBytes());

                // Convertir bytes a formato hexadecimal
                StringBuilder hashStringBuilder = new StringBuilder();
                for (byte hashByte : hashBytes) {
                    hashStringBuilder.append(String.format("%02x", hashByte));
                }

                return hashStringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * función que se encarga de la creación de nuevas cuentas
         *
         * @param usuario    el usuario al que se quiere crear la contraseña         *
         * @param tipoCuenta el tipo de cuenta que se quiere crear
         */
        private void crearCuenta(Usuario usuario, String tipoCuenta) {

            CuentaBancaria nuevaCuenta = new CuentaBancaria(tipoCuenta, usuario);
            cuentaBancariaDao.create(nuevaCuenta);

            System.out.println("Nueva cuenta creada");

            String nuevaCuentaCifrada = FuncionesCifrado.cifrar(nuevaCuenta.getNumeroCuenta(), clientPublicKey);

            try {
                objectOutputStream.writeObject(nuevaCuentaCifrada);
                objectOutputStream.writeObject("Cuenta creada");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * funcíon para la creación de tranferencias entre cuentas de usuario
         *
         * @param numcuenta     numero de cuenta de la que retirar dinero
         * @param cuentaDestino numero de cuenta a la que añadir dinero
         * @param valor         cantidad de dinero
         * @throws IOException
         */
        private void tranferenciacc(String numcuenta, String cuentaDestino, Double valor) throws IOException {

            String numcuentaDes = FuncionesCifrado.descifrar(numcuenta, keypair.getPrivate());
            String cuentaDestinoDes = FuncionesCifrado.descifrar(cuentaDestino, keypair.getPrivate());

            CuentaBancaria cuentaOrigen = cuentaBancariaDao.find(numcuentaDes);
            CuentaBancaria cuentaDestinos = cuentaBancariaDao.find(cuentaDestinoDes);

            Transferencia transferencia = new Transferencia(cuentaDestinoDes, valor, cuentaOrigen);
            if (cuentaBancariaDao.existeCuentaBancaria(numcuentaDes) && cuentaBancariaDao.existeCuentaBancaria(cuentaDestinoDes)) {

                if (cuentaOrigen.getSaldo() <= 0) {
                    System.out.println("Saldo insuficiente");

                } else {
                    try {
                        transferenciaDao.create(transferencia);
                        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - valor);
                        cuentaDestinos.setSaldo(valor + cuentaDestinos.getSaldo());
                        cuentaBancariaDao.update(cuentaOrigen);
                        cuentaBancariaDao.update(cuentaDestinos);
                        System.out.println("Tranferencia correcta");
                        objectOutputStream.writeObject("Tranferencia correcta");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            } else {
                System.out.println("Una de las cuentas no existe");
            }

        }

        /**
         * funcion para la creación de transferencias entre cuenta del usuario y cuenta ajena pero del mismo banco
         * @param numcuenta cuenta de origen del dinero
         * @param cuentaDestino cuenta de destino del dinero
         * @param valor cantidad de dinero
         * @param nombre nombre del titular de la cuenta destino
         * @param apellidos apellidos del titular de la cuenta destino
         * @throws IOException
         */
        private void tranferenciaDir(String numcuenta, String cuentaDestino, Double valor, String nombre, String apellidos) throws IOException {

            String numcuentaDes = FuncionesCifrado.descifrar(numcuenta, keypair.getPrivate());
            String cuentaDestinoDes = FuncionesCifrado.descifrar(cuentaDestino, keypair.getPrivate());

            CuentaBancaria cuentaOrigen = cuentaBancariaDao.find(numcuentaDes);

            Transferencia transferencia = new Transferencia(cuentaDestinoDes, valor, cuentaOrigen);
            if (cuentaBancariaDao.existeCuentaBancaria(numcuentaDes)) {
                if (cuentaOrigen.getSaldo() <= 0) {
                    System.out.println("Saldo insuficiente");

                } else {

                    try {
                        transferenciaDao.create(transferencia);
                        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - valor);
                        cuentaBancariaDao.update(cuentaOrigen);
                        System.out.println("Tranferencia correcta");
                        objectOutputStream.writeObject("Tranferencia correcta");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            } else {
                System.out.println("La cuenta no existe");
            }

        }

        /**
         * Document que se envia al usuario antes de registrarse
         * @throws IOException
         */
        private void DocumentoCondiciones() throws IOException {
            // documento para la firma digital
            String documento = "¡Bienvenido a PocketBank!\n" +
                    "\n" +
                    "Antes de comenzar, por favor, revisa y acepta nuestras condiciones:\n" +
                    "\n" +
                    "Privacidad y Seguridad:\n" +
                    "Tus datos están seguros y protegidos.\n" +
                    "\n" +
                    "Uso Responsable:\n" +
                    "Utiliza la aplicación de manera ética y legal.\n" +
                    "\n" +
                    "Actualizaciones:\n" +
                    "Recibirás mejoras automáticas para un rendimiento óptimo.\n" +
                    "\n" +
                    "Notificaciones:\n" +
                    "Personaliza tus preferencias de notificación.\n" +
                    "\n" +
                    "Responsabilidad del Usuario:\n" +
                    "Mantén segura tu información de inicio de sesión.\n" +
                    "\n" +
                    "Al hacer clic en \"Aceptar\", confirmas tu acuerdo con estas condiciones. ¡Gracias por elegir PocketBank!";

            String firma = FuncionesCifrado.firmar(documento, keypair.getPrivate());

            // Envía el documento y la firma al Usuario
            objectOutputStream.writeObject(documento);
            objectOutputStream.writeObject(firma);
        }
    }
}
