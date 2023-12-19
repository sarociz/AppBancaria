package GUI;

import ClienteServidor.Client;
import ClienteServidor.FuncionesCifrado;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.PrivateKey;

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

    public RegistroGUI() {
        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\logo (2).png");
        icono.setIcon(icon);
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = TFUsuario.getText();
                String contrasena = new String(PFCantrasena.getPassword());
                String nombre = TFNombre.getText();
                String apellido = TFApellidos.getText();
                int edad = Integer.parseInt(TFEdad.getText());
                String correo = TFCorreo.getText();

                if (usuario.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || edad == 0) {
                    JOptionPane.showMessageDialog(null, "Faltan datos", "Registro", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        cliente.objectOutputStream.writeObject(1);
                        cliente.registrar(nombre, apellido, edad, correo, usuario, contrasena);

                        boolean usuarioExiste = (boolean) cliente.objectInputStream.readObject();

                        if (usuarioExiste) {
                            String cuentaCifrada = (String) cliente.objectInputStream.readObject();
                            cliente.objectInputStream.readObject();
                            cliente.objectInputStream.readObject();

                            PrivateKey privateKey = (PrivateKey) cliente.objectInputStream.readObject();

                            String cuentaDescifrada = FuncionesCifrado.descifrar(cuentaCifrada, privateKey);

                            JOptionPane.showMessageDialog(null, "Registro exitoso.\n" +
                                    "El número de cuenta es: " + cuentaDescifrada, "Registro", JOptionPane.INFORMATION_MESSAGE);

                            //baseFrame.dispose();
                            //LogIn.abrirVentanaPrincipal(cliente);
                        } else {
                            String mensaje = (String) cliente.objectInputStream.readObject();
                            JOptionPane.showMessageDialog(null, mensaje, "Registro", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }


            }
        });
    }
    private void recibirDocumento(Client cliente) throws Exception {
        String documentoRecibido = (String) cliente.objectInputStream.readObject();
        String firmaRecibida = (String) cliente.objectInputStream.readObject();

        //boolean firmaValida = FuncionesComunes.verificarFirma(documentoRecibido, firmaRecibida, cliente.clavePublicaServidor);

        aceptarDocumento = false;
//        if (firmaValida) {
//            int respuesta = JOptionPane.showConfirmDialog(null, documentoRecibido + "\n" +
//                    "¿Acepta los términos para utilizar esta aplicación?", "DOCUMENTO", JOptionPane.YES_NO_OPTION);
//
//            if (respuesta == JOptionPane.YES_OPTION) {
//                aceptarDocumento = true;
//            }
//        }
//        cliente.objectOutputStream.writeObject(aceptarDocumento);
    }
    private void limpiarComponentes() {
        TFNombre.setText("");
        TFApellidos.setText("");
        TFEdad.setText("");
        TFUsuario.setText("");
        PFCantrasena.setText("");
    }

    public JTextField getTFNombre() {
        return TFNombre;
    }

    public JTextField getTFApellidos() {
        return TFApellidos;
    }

    public JTextField getTFEdad() {
        return TFEdad;
    }

    public JTextField getTFUsuario() {
        return TFUsuario;
    }

    public JPasswordField getPFCantrasena() {
        return PFCantrasena;
    }

    public JButton getAceptarButton() {
        return aceptarButton;
    }

    public JPanel getPanelRegistro() {
        return panelRegistro;
    }
}
