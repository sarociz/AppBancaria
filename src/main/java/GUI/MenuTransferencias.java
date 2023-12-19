package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public MenuTransferencias() {
        panelTransCuentaCuenta.setVisible(false);
        panelTranfDirectas.setVisible(false);


        buttonCuentaCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelTransCuentaCuenta.setVisible(true);
                panelTranfDirectas.setVisible(false);

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
