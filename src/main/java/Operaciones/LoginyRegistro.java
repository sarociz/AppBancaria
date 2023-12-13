package Operaciones;

import dao.UsuarioDaoImpl;
import models.Usuario;

import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class LoginyRegistro {
    private static Usuario usuario;

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


    private static void registrarUsuario(UsuarioDaoImpl usuarioDao,String nombre, String apellidos, int edad, String nomusu, String contrasena) {
        String hashContrasena = calcularHash(contrasena);

        if (usuarioDao.existeUsuario(nomusu)) {
            JOptionPane.showMessageDialog(null, "Nombre de usuario en uso. Por favor, elija otro.");
            return;
        }
        usuario = new Usuario(nombre, apellidos, edad, nomusu, hashContrasena);
        usuarioDao.create(usuario);
        JOptionPane.showMessageDialog(null,"¡Registro exitoso!");

    }

    public static void iniciarSesion(UsuarioDaoImpl usuarioDao, String nomusu, String contrasena) {

        String hashContrasena = calcularHash(contrasena);

        if (usuarioDao.existeUsuario(nomusu)) {
            // busco el usuario en la base de datos
            usuario = usuarioDao.findByUsuario(nomusu);
            // Check if the username exists and the password matches
            if (usuario.getContrasena() != null && usuario.getContrasena().equals(hashContrasena)) {
                System.out.println("¡Inicio de sesión exitoso!");
            } else {
                System.out.println("Inicio de sesión fallido. Verifique sus credenciales.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado");

        }
        //String storedHashedPassword = usuarioDao.findByContrasena(hashContrasena);


    }

}
