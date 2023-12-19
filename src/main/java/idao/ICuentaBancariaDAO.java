package idao;

import models.CuentaBancaria;

import java.util.List;

public interface ICuentaBancariaDAO {
    boolean existeCuentaBancaria(String numeroCuenta);

    List<CuentaBancaria> findByUsuarioId(int usuarioID);
}
