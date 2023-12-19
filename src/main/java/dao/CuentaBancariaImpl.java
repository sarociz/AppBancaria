package dao;

import idao.ICuentaBancariaDAO;
import jakarta.persistence.TypedQuery;
import models.CuentaBancaria;

import java.util.List;

public class CuentaBancariaImpl extends Dao<CuentaBancaria, String> implements ICuentaBancariaDAO {

    @Override
    public CuentaBancaria find(String numeroCuenta) {
        CuentaBancaria cuentaBancaria = (CuentaBancaria) em.find(CuentaBancaria.class, numeroCuenta);
        return cuentaBancaria;
    }
    @Override
    public boolean existeCuentaBancaria(String numeroCuenta) {
        String jpql = "SELECT COUNT(p) FROM CuentaBancaria p WHERE p.numeroCuenta = :numeroCuenta";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("numeroCuenta", numeroCuenta);

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public List<CuentaBancaria> findByUsuarioId(int usuarioID) {
        String jpql = "SELECT c FROM CuentaBancaria c WHERE c.usuario.id = :usuarioID";
        TypedQuery<CuentaBancaria> query = em.createQuery(jpql, CuentaBancaria.class);
        query.setParameter("usuarioID", usuarioID);
        return query.getResultList();
    }


}
