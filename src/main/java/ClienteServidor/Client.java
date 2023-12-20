package ClienteServidor;

import GUI.LoginGUI;
import dao.CuentaBancariaImpl;
import dao.UsuarioDaoImpl;
import models.CuentaBancaria;
import models.Transferencia;
import models.Usuario;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.swing.*;

/**
 * Clase cliente que se conecta con el servidor y le va enviando peticiones y recibiendo respuestas.
 */
public class Client {
    static UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
    private static int puerto = 12345;
    private static String address = "localhost";
    static KeyPair keyPair;
    public static PublicKey clavePublicaServidor;

    public static Socket socket;
    public static ObjectOutputStream objectOutputStream;

    public static ObjectInputStream objectInputStream;
    CuentaBancariaImpl cuentaBancariaDAO = new CuentaBancariaImpl();


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        socket = new Socket(address, puerto);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        keyPair = FuncionesCifrado.clavesAsimetricas();

        try {
            // Enviar clave pública al servidor
            objectOutputStream.writeObject(keyPair.getPublic());
            objectOutputStream.flush();

            // Recibir clave pública del servidor
            clavePublicaServidor = (PublicKey) objectInputStream.readObject();
            System.out.println(clavePublicaServidor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Mostrar ventana de login
        JFrame frame = new JFrame("Login");
        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\banco.png");
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LoginGUI login = new LoginGUI(usuarioDao, keyPair, clavePublicaServidor);
        frame.setContentPane(login.getPanelLogin());

        // Realiza el ajuste y muestra la ventana
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void iniciarSesion(String nomUsu, String contrasena) {
        try {
            objectOutputStream.writeObject(new Usuario(nomUsu, contrasena));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registrar(String nombre, String apellido, int edad, String correo, String nomusu, String contrasena) {
        try {
            Usuario nuevoUsuario = new Usuario(nombre, apellido,  edad, correo,nomusu, contrasena);

            objectOutputStream.writeObject(nuevoUsuario);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
