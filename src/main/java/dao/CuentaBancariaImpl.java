package dao;

import models.CuentaBancaria;

public class CuentaBancariaImpl extends Dao<CuentaBancaria, Integer> {
    @Override
    public CuentaBancaria find(Integer id) {
        CuentaBancaria cuentaBancaria = (CuentaBancaria) em.find(CuentaBancaria.class, id);
        return cuentaBancaria;
    }
}
