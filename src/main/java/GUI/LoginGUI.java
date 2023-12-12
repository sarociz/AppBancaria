package GUI;

import dao.UsuarioDaoImpl;
import models.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI {
    private JPanel panel1;
    private JTextField TFUsuario;
    private JPasswordField PFContrasena;
    private JButton aceptarButton;
    private JButton cancelarButton;

    public LoginGUI() {
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UsuarioDaoImpl usuarioDAO = new UsuarioDaoImpl();
                Usuario usuario = new Usuario();
                usuario.setUsuario(TFUsuario.getText());
                usuario.setContrasena(new String(PFContrasena.getPassword()));
                usuarioDAO.find(usuario.getId());
                JOptionPane.showMessageDialog(null, "Usuario Logeado");

            }
        });
    }
}
