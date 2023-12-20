package GUI;

import ClienteServidor.Client;
import ClienteServidor.ExpresionesRegulares;
import ClienteServidor.FuncionesCifrado;
import dao.CuentaBancariaImpl;
import models.CuentaBancaria;
import models.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.List;

public class MenuOperaciones {
    private JPanel panelMenuTransferencias;
    private JButton buttonCuentaCuenta;
    private JButton transferenciaDirectaButton;
    private JScrollPane scrollTransferencias;
    private JPanel panelTransferencias;

    //----cuentacuenta----
    private JPanel panelTransCuentaCuenta;
    private JComboBox CBCuentaOrigen;
    private JLabel labelSaldo;
    private JSpinner spinnerValor;
    private JComboBox CBCuentaDestino;
    //--------------------
    //----tranf directa----
    private JPanel panelTranfDirectas;
    private JComboBox CBNumCuenta;
    private JLabel labelSaldoDisponible;
    private JTextField TFCuentaDestino;
    private JTextField TFNombre;
    private JButton aceptarButton;
    private JButton buttonVolver;
    private JSpinner spinnerValorDir;
    private JButton volverButton;
    private JButton aceptarButton1;
    private JButton crearCuentaButton;
    private JPanel panelnuevacuenta;
    private JComboBox CBTipoCuenta;
    private JButton buttonAceptar;
    private JTextField TFApellidos;
    //--------------------
    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 1000000000000.0, 0.1);
    CuentaBancariaImpl cuentaBancariaDao = new CuentaBancariaImpl();

    /**
     * En esta ventana se recogen los datos para realizar las transferencias, los numeros de cuenta se pasarán cifrados
     * para la seguridad de la aplicación
     *
     * @param keyPair            las claves del cliente
     * @param usuario            usuario actualmente logeado
     * @param cuentaBancariaList lista de cuentas del usuario
     */
    public MenuOperaciones(KeyPair keyPair, Usuario usuario, List<CuentaBancaria> cuentaBancariaList, PublicKey claveServidor) {
        panelTransCuentaCuenta.setVisible(false);
        panelTranfDirectas.setVisible(false);
        panelnuevacuenta.setVisible(false);

        //convertir spinners a double
        spinnerValorDir.setModel(spinnerModel);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinnerValorDir.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(2);

        spinnerValor.setModel(spinnerModel);
        JSpinner.NumberEditor editor2 = (JSpinner.NumberEditor) spinnerValor.getEditor();
        DecimalFormat format2 = editor2.getFormat();
        format2.setMinimumFractionDigits(2);

        for (CuentaBancaria cuenta : cuentaBancariaList) {
            CBCuentaOrigen.addItem(cuenta.getNumeroCuenta());
        }

        for (CuentaBancaria cuenta : cuentaBancariaList) {
            CBCuentaDestino.addItem(cuenta.getNumeroCuenta());
        }

        for (CuentaBancaria cuenta : cuentaBancariaList) {
            CBNumCuenta.addItem(cuenta.getNumeroCuenta());
        }

        buttonCuentaCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelTransCuentaCuenta.setVisible(true);
                panelTranfDirectas.setVisible(false);
                panelnuevacuenta.setVisible(false);
            }
        });
        transferenciaDirectaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelTransCuentaCuenta.setVisible(false);
                panelTranfDirectas.setVisible(true);
                panelnuevacuenta.setVisible(false);

            }
        });
        crearCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.objectOutputStream.writeObject(4);
                    panelTransCuentaCuenta.setVisible(false);
                    panelTranfDirectas.setVisible(false);
                    panelnuevacuenta.setVisible(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.objectOutputStream.writeObject(5);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String numCuenta = CBCuentaOrigen.getSelectedItem().toString();
                String cuentaDestino = CBCuentaDestino.getSelectedItem().toString();
                Double valor = (Double) spinnerValor.getValue();

                try {
                    String numCuentaCif = FuncionesCifrado.cifrar(numCuenta, claveServidor);
                    String cuentaDestinoCif = FuncionesCifrado.cifrar(cuentaDestino, claveServidor);
                    Client.objectOutputStream.writeObject(numCuentaCif);//numCuenta cifrado
                    Client.objectOutputStream.writeObject(cuentaDestinoCif);//cuenta destino cifrada
                    Client.objectOutputStream.writeObject(valor);//valor

                    String tranMensaje = (String) Client.objectInputStream.readObject();
                    JOptionPane.showMessageDialog(null, tranMensaje);


                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }


            }
        });
        /**
         * recoger los datos de la transferencia directa y enviarselos al servidor
         */
        aceptarButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.objectOutputStream.writeObject(6);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String numCuenta = CBCuentaOrigen.getSelectedItem().toString();
                String cuentaDestino = TFCuentaDestino.getText();
                Double valor = (Double) spinnerValor.getValue();
                String nombre = TFNombre.getText();
                String apellidos = TFApellidos.getText();

                if (valor == 0 || nombre.isEmpty() || apellidos.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Rellena todos los campos", "Campos vacios", JOptionPane.ERROR_MESSAGE);
                }else if(!ExpresionesRegulares.comprobarNumCuenta(cuentaDestino)){
                    JOptionPane.showMessageDialog(null, "El número de cuenta destino tiene que tener 12 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    try {
                        String numCuentaCif = FuncionesCifrado.cifrar(numCuenta, claveServidor);
                        String cuentaDestinoCif = FuncionesCifrado.cifrar(cuentaDestino, claveServidor);
                        Client.objectOutputStream.writeObject(numCuentaCif);//numCuenta cifrado
                        Client.objectOutputStream.writeObject(cuentaDestinoCif);//cuenta destino cifrada
                        Client.objectOutputStream.writeObject(valor);//valor
                        Client.objectOutputStream.writeObject(nombre);//valor
                        Client.objectOutputStream.writeObject(apellidos);//valor

                        String tranMensaje = (String) Client.objectInputStream.readObject();
                        JOptionPane.showMessageDialog(null, tranMensaje);


                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        /**
         * al seeccionar el botón, se envian todos los datos correspondientes al servidor, para la creación de la cuenta
         * aqui se descifra el numero de cuenta enviado por el servidor de la cuenta nueva.
         */
        buttonAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipoCuenta = CBTipoCuenta.getSelectedItem().toString();
                try {
                    Client.objectOutputStream.writeObject(usuario);
                    Client.objectOutputStream.writeObject(tipoCuenta);

                    String CuentaCifrada = (String) Client.objectInputStream.readObject();
                    String cuentaDescifrada = FuncionesCifrado.descifrar(CuentaCifrada, keyPair.getPrivate());

                    CuentaBancariaImpl cuentaBancariaDao = new CuentaBancariaImpl();
                    CuentaBancaria cuentaBancaria = cuentaBancariaDao.find(cuentaDescifrada);
                    cuentaBancariaList.add(cuentaBancaria);


                    String mensaje = (String) Client.objectInputStream.readObject();
                    JOptionPane.showMessageDialog(null, mensaje);
                    panelnuevacuenta.setVisible(false);


                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        buttonVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelTransCuentaCuenta.setVisible(false);
                try {
                    Client.objectOutputStream.writeObject(3);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        CBCuentaOrigen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numCuenta = CBCuentaOrigen.getSelectedItem().toString();

                labelSaldo.setText(String.valueOf(cuentaBancariaDao.find(numCuenta).getSaldo()));
            }
        });
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelTranfDirectas.setVisible(false);
                try {
                    Client.objectOutputStream.writeObject(3);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        CBNumCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numCuenta = CBNumCuenta.getSelectedItem().toString();
                labelSaldoDisponible.setText(String.valueOf(cuentaBancariaDao.find(numCuenta).getSaldo()));

            }
        });
    }

    public JButton getButtonCuentaCuenta() {
        return buttonCuentaCuenta;
    }

    public void setButtonCuentaCuenta(JButton buttonCuentaCuenta) {
        this.buttonCuentaCuenta = buttonCuentaCuenta;
    }

    public JButton getTransferenciaDirectaButton() {
        return transferenciaDirectaButton;
    }

    public void setTransferenciaDirectaButton(JButton transferenciaDirectaButton) {
        this.transferenciaDirectaButton = transferenciaDirectaButton;
    }

    public JPanel getPanelMenuTransferencias() {
        return panelMenuTransferencias;
    }
}
