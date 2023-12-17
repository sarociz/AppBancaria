import ClienteServidor.Servidor;
import GUI.LoginGUI;
import GUI.MenuTransferencias;
import dao.CuentaBancariaImpl;
import dao.UsuarioDaoImpl;
import models.CuentaBancaria;
import models.Usuario;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import static ClienteServidor.Servidor.startServidor;
import static Operaciones.LoginyRegistro.registrarUsuario;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        CuentaBancariaImpl cuentaBancariaDao = new CuentaBancariaImpl();
        UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
        Usuario usuario = new Usuario();
        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();
        CuentaBancaria cuentaBancaria = new CuentaBancaria();
        CuentaBancaria cuentaBancaria1 = new CuentaBancaria();
        CuentaBancaria cuentaBancaria2 = new CuentaBancaria();

        //usuario = new Usuario("Sara", "Ocasar", 25, "sara123", "contrasena123");
//        usuario1 = new Usuario("Aroa", "Rodriguez", 25, "arisun", "123");
//        usuario2 = new Usuario("admin", "admin", 25, "admin", "123");
        //registrarUsuario(usuarioDao,usuario);
//        registrarUsuario(usuarioDao,usuario1);
//        registrarUsuario(usuarioDao,usuario2);
//        usuario = usuarioDao.find(1);
//        usuario1 = usuarioDao.find(2);
//        usuario2 = usuarioDao.find(3);
//       cuentaBancaria = new CuentaBancaria("123456788", 0.0, "Nómina", new Date(), usuario);
//       cuentaBancaria1 = new CuentaBancaria("457869542", 0.0, "Ahorro", new Date(), usuario1);
//       cuentaBancaria2 = new CuentaBancaria("412369856", 0.0, "Corriente", new Date(), usuario2);
////
//        cuentaBancariaDao.create(cuentaBancaria);
//        cuentaBancariaDao.create(cuentaBancaria1);
//        cuentaBancariaDao.create(cuentaBancaria2);

        // Crea una instancia de JFrame
        JFrame frame = new JFrame("Login");
        //JFrame frame2 = new JFrame("Transferencias");
        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\banco.png");
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LoginGUI login = new LoginGUI(usuarioDao);
        MenuTransferencias menuTransferencias = new MenuTransferencias();
        frame.setContentPane(login.getPanelLogin());
        //frame2.setContentPane(menuTransferencias.getPanelMenuTransferencias());

        // Realiza el ajuste y muestra la ventana
        frame.pack();
        //frame2.pack();
        frame.setLocationRelativeTo(null);
        //frame2.setLocationRelativeTo(null);
        frame.setVisible(true);
        //frame2.setVisible(true);
        //startServidor();



    }
}
