package GUI;

import ClienteServidor.Client;
import ClienteServidor.FuncionesCifrado;
import dao.CuentaBancariaImpl;
import models.CuentaBancaria;
import models.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyPair;
import java.text.DecimalFormat;
import java.util.List;

public class MenuTransferencias {
    private JPanel panelMenuTransferencias;
    private JButton buttonCuentaCuenta;
    private JButton transferenciaDirectaButton;
    private JButton transferenciaInterbancariaButton;
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
    private JTextField TFNombreApell;
    private JButton aceptarButton;
    private JButton buttonVolver;
    private JSpinner spinnerValorDir;
    private JButton volverButton;
    private JButton aceptarButton1;
    //--------------------
    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 1000000000000.0, 0.1);

    public MenuTransferencias(KeyPair keyPair, Usuario usuario, List<CuentaBancaria> cuentaBancariaList) {
        panelTransCuentaCuenta.setVisible(false);
        panelTranfDirectas.setVisible(false);
        spinnerValorDir.setModel(spinnerModel);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinnerValorDir.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(2); // Establecer el número mínimo de dígitos fraccionarios

        buttonCuentaCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelTransCuentaCuenta.setVisible(true);
                panelTranfDirectas.setVisible(false);
                String numCuenta = CBCuentaOrigen.getSelectedItem().toString();
                String cuentaDestino = CBCuentaDestino.getSelectedItem().toString();
                Double valor = (Double) spinnerValorDir.getValue();
                if (valor == 0){
                    JOptionPane.showMessageDialog(null, "Ingresa una cantidad", "Transferencia", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Client.objectOutputStream.writeObject(5);
                        String numCuentaCif = FuncionesCifrado.cifrar(numCuenta, keyPair.getPublic());
                        String cuentaDestinoCif = FuncionesCifrado.cifrar(cuentaDestino, keyPair.getPublic());
                        Client.objectOutputStream.writeObject(numCuentaCif);//numCuenta cifrado
                        Client.objectOutputStream.writeObject(cuentaDestinoCif);//cuenta destino cifrada
                        Client.objectOutputStream.writeObject(valor);//valor

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }


            }
        });
        transferenciaDirectaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelTransCuentaCuenta.setVisible(false);
                panelTranfDirectas.setVisible(true);

            }
        });

        buttonVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


    public JPanel getPanelMenuTransferencias() {
        return panelMenuTransferencias;
    }
}
