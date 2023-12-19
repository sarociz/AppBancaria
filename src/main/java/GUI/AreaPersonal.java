package GUI;

import ClienteServidor.Client;
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
import java.util.List;

public class AreaPersonal {
    private JPanel panelAreaPersonal;
    private JTable tablaCuentas;
    private JButton buttonNuevaCuenta;
    private JButton buttonTranferencia;
    private JLabel iconoPersona;
    private JComboBox CBTipoCuenta;
    private JButton buttonAceptar;
    private JButton buttonCancelar;
    private JLabel labelNombreApellidos;
    private JPanel panelnuevacuenta;
    private JLabel logo;
    private DefaultTableModel modelo = new DefaultTableModel();

    public AreaPersonal(Client client, Usuario usuario, List<CuentaBancaria> cuentaBancariaList) {
        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\transferencia-de-archivos.png");
        buttonTranferencia.setIcon(icon);
        ImageIcon icon1 = new ImageIcon(".\\src\\main\\java\\imagenes\\agregar.png");
        buttonNuevaCuenta.setIcon(icon1);
        ImageIcon icon2 = new ImageIcon(".\\src\\main\\java\\imagenes\\avatar1.png");
        iconoPersona.setIcon(icon2);
        ImageIcon icon3 = new ImageIcon(".\\src\\main\\java\\imagenes\\logov2.png");
        logo.setIcon(icon3);

        panelnuevacuenta.setVisible(false);

        labelNombreApellidos.setText(usuario.getUsuario() + " " + usuario.getApellidos());
        buttonNuevaCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelnuevacuenta.setVisible(false);

            }
        });
        buttonTranferencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuTransferencias menuTransferencias = new MenuTransferencias();

                JFrame frame = new JFrame("Transferencias");
                frame.setContentPane(menuTransferencias.getPanelMenuTransferencias());

                // Realiza el ajuste y muestra la ventana
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        buttonAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }
    public void limpiarCampos() {
        CBTipoCuenta.setSelectedItem(0);

    }

    public void actualizarTabla(CuentaBancariaImpl cuentaBancariaDao, List<CuentaBancaria> cuentaBancariaList, Usuario usuario) {
        cuentaBancariaList = cuentaBancariaDao.findByUsuarioId(usuario.getId());
        verTablaGestiones(cuentaBancariaList);
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
