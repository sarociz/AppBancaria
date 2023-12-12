package dao;

import idao.IUsuarioDAO;
import jakarta.persistence.TypedQuery;
import models.Usuario;

public class UsuarioDaoImpl extends Dao<Usuario, Integer> implements IUsuarioDAO{
    @Override
    public Usuario find(Integer id) {
        Usuario usuario = (Usuario) em.find(Usuario.class, id);
        return usuario;
    }

    @Override
    public boolean existeUsuario(Integer id) {
        String jpql = "SELECT COUNT(p) FROM Usuario p WHERE p.id = :id";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("id", id);

        Long count = query.getSingleResult();
        return count > 0;
    }
}
