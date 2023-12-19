package GUI;

import ClienteServidor.Client;
import dao.UsuarioDaoImpl;
import models.CuentaBancaria;
import models.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyPair;
import java.util.List;


import static Operaciones.LoginyRegistro.iniciarSesion;

public class LoginGUI {
    private JPanel panelLogin;
    private JTextField TFUsuario;
    private JPasswordField PFContrasena;
    private JButton aceptarButton;
    private JButton cancelarButton;
    private JPanel PanelLogoLabels;
    private JButton registrarButton;
    private JLabel labelLogo;

    Usuario usuario;

    public JPanel getPanelLogin() {
        return panelLogin;
    }
    private final Client client;
    List<CuentaBancaria> cuentaBancariaList;

    public LoginGUI(UsuarioDaoImpl usuarioDAO, KeyPair keyPair) {

        client = new Client();
        //client.setOutputStream(obtenerObjectOutputStreamDelServidor());

        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\logo (2).png");
        labelLogo.setIcon(icon);
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usuario = new Usuario();
                String nomusu = TFUsuario.getText();
                String contrasena = new String(PFContrasena.getPassword());
                boolean userExists = false;

                if (TFUsuario.getText().isEmpty() || new String(PFContrasena.getPassword()).isEmpty()){
                    JOptionPane.showMessageDialog(null, "Faltan datos");
                    return;
                }else{
                    try {
                        //se envia al servidor que se quiere iniciar sesión
                        Client.objectOutputStream.writeObject(1);
                        client.iniciarSesion(nomusu, contrasena);

                        userExists = (boolean) Client.objectInputStream.readObject();//recibir si existe o no
                        usuario = (Usuario) Client.objectInputStream.readObject();
                        cuentaBancariaList = (List<CuentaBancaria>) Client.objectInputStream.readObject();

                        if (userExists){
                            AreaPersonal areaPersonal = new AreaPersonal(keyPair, usuario, cuentaBancariaList);
                            JFrame frame = new JFrame("Área Personal");
                            frame.setContentPane(areaPersonal.getPanelAreaPersonal());

                            // Realiza el ajuste y muestra la ventana
                            frame.pack();
                            frame.setLocationRelativeTo(null);
                            frame.setVisible(true);
                        }else {
                            JOptionPane.showMessageDialog(null, "Inicio de sesión fallido", "Iniciar Sesión", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (IOException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }

                }

            }
        });
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.objectOutputStream.writeObject(2);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                RegistroGUI registroGUI = null;
                try {
                    registroGUI = new RegistroGUI(keyPair, usuarioDAO);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                JFrame frame = new JFrame("Registro");
                frame.setContentPane(registroGUI.getPanelRegistro());

                // Realiza el ajuste y muestra la ventana
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
