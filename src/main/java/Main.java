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

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        CuentaBancariaImpl cuentaBancariaDao = new CuentaBancariaImpl();
        UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
        Usuario usuario = new Usuario();
        CuentaBancaria cuentaBancaria = new CuentaBancaria();

//        usuario = new Usuario("Juan", "Pérez", 25, "juanito123", "contrasena123");
//        cuentaBancaria = new CuentaBancaria("123456789", 1000.0, "Cuenta Corriente", new Date(), usuario);
//
//        cuentaBancariaDao.create(cuentaBancaria);
        //crear usuario
//        System.out.println("Introduce el nombre");
//        usuario.setNombre(br.readLine());
//        System.out.println("Introduce el apellido");
//        usuario.setApellidos(br.readLine());
//        System.out.println("Introduce el correo electronico");
//        usuario.setCorreoElectronico(br.readLine());
//        System.out.println("Introduce el password");
//        usuario.setPassword(br.readLine());
//        usuarioDao.save(usuario);
//
//        //crear cuenta bancaria
//        System.out.println("Introduce el numero de cuenta");
//        cuentaBancaria.setNumeroCuenta(br.readLine());
//        System.out.println("Introduce el saldo");

        // Crea una instancia de JFrame
        JFrame frame = new JFrame("Login");
        JFrame frame2 = new JFrame("Transferencias");
        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\banco.png");
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LoginGUI login = new LoginGUI(usuarioDao);
        MenuTransferencias menuTransferencias = new MenuTransferencias();
        frame.setContentPane(login.getPanelLogin());
        frame2.setContentPane(menuTransferencias.getPanelMenuTransferencias());

        // Realiza el ajuste y muestra la ventana
        frame.pack();
        frame2.pack();
        frame.setLocationRelativeTo(null);
        frame2.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame2.setVisible(true);

    }
}
