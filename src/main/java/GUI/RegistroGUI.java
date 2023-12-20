package GUI;

import ClienteServidor.Client;
import ClienteServidor.ExpresionesRegulares;
import ClienteServidor.FuncionesCifrado;
import dao.UsuarioDaoImpl;
import models.CuentaBancaria;
import models.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class RegistroGUI {
    private JPanel panelRegistro;
    private JTextField TFNombre;
    private JTextField TFApellidos;
    private JTextField TFEdad;
    private JTextField TFUsuario;
    private JPasswordField PFCantrasena;
    private JButton aceptarButton;
    private JTextField TFCorreo;
    private JLabel icono;
    Client cliente = new Client();
    private boolean aceptarDocumento = false;
    List<CuentaBancaria> cuentaBancariaList;

    public RegistroGUI(KeyPair keyPair, UsuarioDaoImpl usuarioDao, PublicKey claveServidor) throws Exception {
        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\logov2.png");
        icono.setIcon(icon);
        recibirDocumento();
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomusuario = TFUsuario.getText();
                String contrasena = new String(PFCantrasena.getPassword());
                String nombre = TFNombre.getText();
                String apellido = TFApellidos.getText();
                int edad = Integer.parseInt(TFEdad.getText());
                String correo = TFCorreo.getText();

                    try {
                        if (TFApellidos.getText().isEmpty() || TFApellidos.getText().isEmpty() || TFEdad.getText().isEmpty() || TFCorreo.getText().isEmpty() || TFUsuario.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Campos vacios", "Registro", JOptionPane.ERROR_MESSAGE);

                        }else if (!ExpresionesRegulares.comprobarCorreo(correo)) {
                            JOptionPane.showMessageDialog(null, "Correo incorrecto. " +
                                    "Tiene que tener esta estructura: xxxx@xxx.com", "Error", JOptionPane.ERROR_MESSAGE);
                        }else if (!ExpresionesRegulares.comprobarContrasena(contrasena)) {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta. \n" +
                                    "-La contraseña debe contener al menos un dígito.\n" +
                                    "-La contraseña debe contener al menos una letra minúscula.\n" +
                                    "-La contraseña debe contener al menos una letra mayúscula.\n" +
                                    "-La contraseña no debe contener espacios en blanco.\n" +
                                    "-La contraseña debe tener al menos 8 caracteres de longitud.", "Error", JOptionPane.ERROR_MESSAGE);
                        }else {
                            Client.registrar(nomusuario, apellido, edad, correo, nombre, apellido);

                            boolean usuarioExiste = (boolean) Client.objectInputStream.readObject();
                            Usuario usuario = (Usuario) Client.objectInputStream.readObject();
                            cuentaBancariaList = (List<CuentaBancaria>) Client.objectInputStream.readObject();

                            if (usuarioExiste) {
                                AreaPersonal areaPersonal = new AreaPersonal(keyPair, usuario, cuentaBancariaList, claveServidor);
                                JFrame frame = new JFrame("Área Personal");
                                frame.setContentPane(areaPersonal.getPanelAreaPersonal());

                                // Realiza el ajuste y muestra la ventana
                                frame.pack();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);

                            } else {
                                JOptionPane.showMessageDialog(null, "Registro incorrecto", "Registro", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }




            }


        });
    }

    private void recibirDocumento() throws Exception {
        String documentoRecibido = (String) Client.objectInputStream.readObject();
        String firmaRecibida = (String) Client.objectInputStream.readObject();

        boolean firmaValida = FuncionesCifrado.verificarFirma(documentoRecibido, firmaRecibida, Client.clavePublicaServidor);

        aceptarDocumento = false;
        if (firmaValida) {
            int respuesta = JOptionPane.showConfirmDialog(null, documentoRecibido, "Terminos de uso", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                aceptarDocumento = true;
            }
        }
        Client.objectOutputStream.writeObject(aceptarDocumento);


    }

    private void limpiarComponentes() {
        TFNombre.setText("");
        TFApellidos.setText("");
        TFEdad.setText("");
        TFUsuario.setText("");
        PFCantrasena.setText("");
    }


    public JPanel getPanelRegistro() {
        return panelRegistro;
    }
}
