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
import java.util.Collections;
import java.util.List;

import static ClienteServidor.FuncionesCifrado.*;

public class Server {
    private static final KeyPair keypair;

    static {
        try {
            keypair = generarParDeClaves();
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

    private static class ClienteHandler implements Runnable {
        private final Socket socket;
        private ObjectOutputStream objectOutputStream;
        private final ObjectInputStream objectInputStream;
        private final PrivateKey privateKey;
        public PublicKey publicKey;
        private CuentaBancariaImpl cuentaBancariaDao;
        private TransferenciaDaoImpl transferenciaDao;
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
            ;
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
                                    tranferenciacc();
                                    break;
                                case TRANSFERENCIAD:
                                    //verSaldo(usuario, operacion);
                                    break;
                            }

                            // Recoger el tipo de operación y provesarla.
//                                OperacionBancaria operacion = (OperacionBancaria) entrada.readObject();
//                                procesarOperacion(usuarioActual, operacion);

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


            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            } finally {
                cerrarConexiones();
            }
        }

        private void RegistroLogin(boolean userExists, int botonPulsado) throws IOException, ClassNotFoundException {
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


        private void cerrarConexiones() {

            try {
                objectInputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

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

        private void tranferenciacc(String numcuenta, String cuentaDestino, Double valor) {

            String numcuentaDes = FuncionesCifrado.descifrar(numcuenta, keypair.getPrivate());
            String cuentaDestinoDes = FuncionesCifrado.descifrar(cuentaDestino, keypair.getPrivate());

            CuentaBancaria cuenta = cuentaBancariaDao.find(numcuentaDes);

            Transferencia transferencia = new Transferencia(numcuentaDes, valor, cuenta);
            if (cuenta.getSaldo() <= 0) {
                System.out.println("Saldo insuficiente");
                try {
                    objectOutputStream.writeObject("Saldo insuficiente");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                try {
                    transferenciaDao.create(transferencia);
                    cuenta.setSaldo(valor + cuenta.getSaldo());
                    cuentaBancariaDao.update(cuenta);
                    System.out.println("Tranferencia correcta");
                    objectOutputStream.writeObject("Tranferencia correcta");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

        }

        private void transCuentaCuenta(Transferencia transferencia) {
            try {
                String primeraCuentaCifrada = operacion.getCuentaOrigen();
                String segundaCuentaCifrada = operacion.getCuentaDestino();

                String priCueDescifrada = FirmadorDigital.descifrar(primeraCuentaCifrada, parDeClavesServidor.getPrivate());
                String segCueDescifrada = FirmadorDigital.descifrar(segundaCuentaCifrada, parDeClavesServidor.getPrivate());

                // Obtener cuentas del usuario
                CuentaBancaria cuentaOrigen = usuario.obtenerCuentaPorNumero(priCueDescifrada);
                CuentaBancaria cuentaDestino = usuario.obtenerCuentaPorNumero(segCueDescifrada);

                // Verificar saldo suficiente en la cuenta origen
                if (cuentaOrigen.getSaldo() >= operacion.getMonto()) {
                    salida.writeObject(true);

                    // Cifrar el código generado aleatorioamente
                    String codigoOriginal = String.valueOf(generadorCodigoAleatorio());
                    String codigoCifrado = FirmadorDigital.cifrar(codigoOriginal, usuario.getClavePublica());

                    // Envía el código cifrado al Usuario
                    salida.writeObject(codigoCifrado);

                    // Recibir la respuesta del Usuario
                    String codigoDescifrado = (String) entrada.readObject();

                    // Verifica si el código descifrado coincide con el original
                    if (codigoOriginal.equals(codigoDescifrado)) {
                        // Realizar la transferencia
                        cuentaOrigen.retirar(operacion.getMonto());
                        cuentaDestino.depositar(operacion.getMonto());

                        cuentaBancariaDao.update(cuentaOrigen);
                        cuentaBancariaDao.update(cuentaDestino);

                        System.out.println("Código correcto. Transferencia aceptada.");
                        salida.writeObject("Transferencia realizada con éxito.");
                    } else {
                        salida.writeObject(false);
                        // Código incorrecto, rechaza la transferencia
                        salida.writeObject("Código incorrecto. Transferencia rechazada.");
                        System.out.println("Código incorrecto. Transferencia rechazada.");
                    }
                } else {
                    salida.writeObject(false);
                    salida.writeObject("Saldo insuficiente en la cuenta origen.");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


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
