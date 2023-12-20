import dao.CuentaBancariaImpl;
import dao.TransferenciaDaoImpl;
import dao.UsuarioDaoImpl;
import models.CuentaBancaria;
import models.Transferencia;
import models.Usuario;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static ClienteServidor.Server.ClienteHandler.calcularHash;


public class InsertDatos {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
        CuentaBancariaImpl cuentaBancariaDao = new CuentaBancariaImpl();
        TransferenciaDaoImpl transferenciaDao = new TransferenciaDaoImpl();

        Usuario usuario = new Usuario();
        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();
        CuentaBancaria cuentaBancaria = new CuentaBancaria();
        CuentaBancaria cuentaBancaria1 = new CuentaBancaria();
        CuentaBancaria cuentaBancaria2 = new CuentaBancaria();
        CuentaBancaria cuentaBancaria3 = new CuentaBancaria();
        Transferencia transferencia = new Transferencia();
        Transferencia transferencia2 = new Transferencia();
        Transferencia transferencia3 = new Transferencia();

        usuario = new Usuario("Sara", "Ocasar", 25,"s@gmail.com", "sara123", "123");
        usuario1 = new Usuario("Aroa", "Rodriguez", 25, "a@gmail.com","arisun", "123");
        usuario2 = new Usuario("admin", "admin", 25, "admin@gmail.com","admin", "123");
        registrarUsuario(usuarioDao, usuario);
        registrarUsuario(usuarioDao, usuario1);
        registrarUsuario(usuarioDao, usuario2);
        usuario = usuarioDao.find(1);
        usuario1 = usuarioDao.find(2);
        usuario2 = usuarioDao.find(3);
        cuentaBancaria = new CuentaBancaria("Nómina", usuario);
        cuentaBancaria1 = new CuentaBancaria("Ahorro",  usuario1);
        cuentaBancaria2 = new CuentaBancaria("Corriente", usuario2);
        cuentaBancaria3 = new CuentaBancaria("Ahorro", usuario2);

        cuentaBancariaDao.create(cuentaBancaria);
        cuentaBancariaDao.create(cuentaBancaria1);
        cuentaBancariaDao.create(cuentaBancaria2);
        cuentaBancariaDao.create(cuentaBancaria3);

        String numcuenta = cuentaBancaria.getNumeroCuenta();
        String numcuenta1 = cuentaBancaria1.getNumeroCuenta();
        String numcuenta2 = cuentaBancaria2.getNumeroCuenta();
        String numcuenta3 = cuentaBancaria3.getNumeroCuenta();

        cuentaBancaria = cuentaBancariaDao.find(numcuenta);
        cuentaBancaria1 = cuentaBancariaDao.find(numcuenta1);
        cuentaBancaria2 = cuentaBancariaDao.find(numcuenta2);
        cuentaBancaria3 = cuentaBancariaDao.find(numcuenta2);

        transferencia = new Transferencia("451258754896", 1000.0, "Alba",  "Alonso", cuentaBancaria);
        transferencia2 = new Transferencia(numcuenta3, 1000.0,  cuentaBancaria2);
        transferencia3 = new Transferencia("475852147785", 1000.0, "Miguel",  "Acosta", cuentaBancaria1);

        transferenciaDao.create(transferencia);
        transferenciaDao.create(transferencia2);
        transferenciaDao.create(transferencia3);


    }
    public static void registrarUsuario(UsuarioDaoImpl usuarioDao, Usuario usuario) {
        String hashContrasena = calcularHash(usuario.getContrasena());

        if (usuarioDao.existeUsuario(usuario.getUsuario())) {
            JOptionPane.showMessageDialog(null, "Nombre de usuario en uso. Por favor, elija otro.");
            return;
        }
        usuario.setContrasena(hashContrasena);

        usuarioDao.create(usuario);
        JOptionPane.showMessageDialog(null,"¡Registro exitoso!");

    }
}
