package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AreaPersonal {
    private JPanel panelAreaPersonal;
    private JTable table1;
    private JButton buttonNuevaCuenta;
    private JButton buttonTranferencia;
    private JButton buttonSalir;
    private JLabel iconoPersona;

    public AreaPersonal() {
        ImageIcon icon = new ImageIcon(".\\src\\main\\java\\imagenes\\transferencia-de-archivos.png");
        buttonTranferencia.setIcon(icon);
        ImageIcon icon1 = new ImageIcon(".\\src\\main\\java\\imagenes\\agregar.png");
        buttonNuevaCuenta.setIcon(icon1);
        buttonNuevaCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonTranferencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getPanelAreaPersonal() {
        return panelAreaPersonal;
    }
}
