package dao;

import idao.ITransferenciaDAO;
import idao.IUsuarioDAO;
import models.Transferencia;
import models.Usuario;

public class TransferenciaDaoImpl extends Dao<Transferencia, Long> implements ITransferenciaDAO {
    @Override
    public Transferencia find(Long id) {
        return (Transferencia) em.find(Transferencia.class, id);
    }

}
