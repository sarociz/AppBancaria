package GUI;

import ClienteServidor.Client;
import ClienteServidor.FuncionesCifrado;
import dao.CuentaBancariaImpl;
import models.CuentaBancaria;
import models.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;

public class AreaPersonal {
    private JPanel panelAreaPersonal;
    private JTable tablaCuentas;
    private JButton buttonMenuOp;
    private JLabel iconoPersona;
    private JComboBox CBTipoCuenta;
    private JButton buttonAceptar;
    private JButton buttonCancelar;
    private JLabel labelNombreApellidos;
    private JPanel panelnuevacuenta;
    private JLabel logo;
    private DefaultTableModel modelo = new DefaultTableModel();

    /**
     * ventana donde se muestran las cuentas bancarias del usuario
     *
     * @param keyPair claves del usuario
     * @param usuario usuario actual logeado
     * @param cuentaBancariaList lista de cuentas bancarias del usuario
     * @param claveServidor clave pública del servidor
     */
    public AreaPersonal(KeyPair keyPair, Usuario usuario, List<CuentaBancaria> cuentaBancariaList, PublicKey claveServidor) {
        ImageIcon icon2 = new ImageIcon(".\\src\\main\\java\\imagenes\\avatar1.png");
        iconoPersona.setIcon(icon2);
        ImageIcon icon3 = new ImageIcon(".\\src\\main\\java\\imagenes\\logov2.png");
        logo.setIcon(icon3);

        verTablaGestiones(cuentaBancariaList);

        labelNombreApellidos.setText(usuario.getUsuario() + " " + usuario.getApellidos());

        buttonMenuOp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.objectOutputStream.writeObject(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                MenuOperaciones menuTransferencias = new MenuOperaciones(keyPair, usuario, cuentaBancariaList, claveServidor);

                JFrame frame = new JFrame("Transferencias");
                frame.setContentPane(menuTransferencias.getPanelMenuTransferencias());

                // Realiza el ajuste y muestra la ventana
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });



    }

    public void verTablaGestiones(List<CuentaBancaria> cuentaBancariaList) {
        String[] columnNames = {"Número", "Tipo", "Saldo"};

        modelo = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer que todas las celdas no sean editables
                return false;
            }
        };

        for (CuentaBancaria cuentaBancaria : cuentaBancariaList) {
            Object[] rowData = {cuentaBancaria.getNumeroCuenta(), cuentaBancaria.getTipoCuenta(), cuentaBancaria.getSaldo()};
            modelo.addRow(rowData);
        }
        tablaCuentas.setModel(modelo);
        JTableHeader header = tablaCuentas.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new Color(140, 140, 143, 255));
        header.setForeground(Color.white);

        tablaCuentas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Cambiar el color de fondo para las filas impares
                if (row % 2 == 1) {
                    renderer.setBackground(new Color(215, 212, 212, 255));
                } else {
                    renderer.setBackground(table.getBackground());
                }
                return renderer;
            }
        });

    }

    public JPanel getPanelAreaPersonal() {
        return panelAreaPersonal;
    }
}
