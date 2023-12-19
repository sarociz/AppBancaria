package Operaciones;

import dao.UsuarioDaoImpl;
import models.Usuario;

import javax.swing.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class pruba {
    private static Usuario usuario;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();

        while (true) {
            System.out.println("1. Registrar Usuario");
            System.out.println("2. Iniciar Sesión");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del número

            switch (opcion) {
                case 1:
                    registrarUsuario(usuarioDao);
                    break;
                case 2:
                    iniciarSesion(usuarioDao);
                    break;
                case 3:
                    System.out.println("Saliendo del programa.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        }
    }


    private static String calcularHash(String contrasena) {
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


    private static void registrarUsuario(UsuarioDaoImpl usuarioDao) {
        // ... (unchanged code)
        Scanner scanner = new Scanner(System.in);

        System.out.println("Registro de Usuario");
        System.out.print("Ingrese su identificador:");
        String identificador = scanner.nextLine();

        System.out.print("Ingrese su contraseña: ");
        String contrasena = scanner.nextLine();

        String hashContrasena = calcularHash(contrasena);

        // Check if the username already exists in the database
        if (usuarioDao.existeUsuario(identificador)) {
            System.out.println("Nombre de usuario en uso. Por favor, elija otro.");
            return;
        }
        usuario = new Usuario("aroa", "rodriguez", 21, "a@gmail.com", identificador, hashContrasena);
        usuarioDao.create(usuario);
        System.out.println("¡Registro exitoso!");

    }

    private static void iniciarSesion(UsuarioDaoImpl usuarioDao) {
        // ... (unchanged code)
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese su identificador: ");
        String identificador = scanner.nextLine();

        System.out.print("Ingrese su contraseña: ");
        String contrasena = scanner.nextLine();

        String hashContrasena = calcularHash(contrasena);

        if (usuarioDao.existeUsuario(identificador)) {
            // busco el usuario en la base de datos
            usuario = usuarioDao.findByUsuario(identificador);
            // Check if the username exists and the password matches
            if (usuario.getContrasena() != null && usuario.getContrasena().equals(hashContrasena)) {
                System.out.println("¡Inicio de sesión exitoso!");
            } else {
                System.out.println("Inicio de sesión fallido. Verifique sus credenciales.");
            }
        }else {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado");

        }
        //String storedHashedPassword = usuarioDao.findByContrasena(hashContrasena);



    }

//    private static boolean checkUsernameExists(String username) throws SQLException {
//        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//            preparedStatement.setString(1, username);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            resultSet.next();
//            return resultSet.getInt(1) > 0;
//        }
//    }

//    private static void insertUserCredentials( String username, String hashedPassword) throws SQLException {
//        String insertSQL = "INSERT INTO users (username, password) VALUES (?, ?)";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
//            preparedStatement.setString(1, username);
//            preparedStatement.setString(2, hashedPassword);
//            preparedStatement.executeUpdate();
//        }
//    }
//
//    private static String getStoredHashedPassword(Connection connection, String username) throws SQLException {
//        String query = "SELECT password FROM users WHERE username = ?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//            preparedStatement.setString(1, username);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            return resultSet.next() ? resultSet.getString("password") : null;
//        }
//    }
}
