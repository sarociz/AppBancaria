package GUI;

import dao.UsuarioDaoImpl;
import models.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public LoginGUI(UsuarioDaoImpl usuarioDAO) {
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usuario = new Usuario();
                usuario.setUsuario(TFUsuario.getText());
                usuario.setContrasena(new String(PFContrasena.getPassword()));
                if (TFUsuario.getText().isEmpty() || new String(PFContrasena.getPassword()).isEmpty()){
                    JOptionPane.showMessageDialog(null, "Faltan datos");
                    return;
                }else{
                    iniciarSesion(usuarioDAO, TFUsuario.getText(), new String(PFContrasena.getPassword()));
                    AreaPersonal areaPersonal = new AreaPersonal();

                    JFrame frame = new JFrame("Área Personal");
                    frame.setContentPane(areaPersonal.getPanelAreaPersonal());

                    // Realiza el ajuste y muestra la ventana
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
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
                RegistroGUI registroGUI = new RegistroGUI();

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
