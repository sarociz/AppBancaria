package dao;

import idao.IUsuarioDAO;
import jakarta.persistence.TypedQuery;
import models.Usuario;

import java.util.List;

public class UsuarioDaoImpl extends Dao<Usuario, Integer> implements IUsuarioDAO{
    @Override
    public Usuario find(Integer id) {
        Usuario usuario = (Usuario) em.find(Usuario.class, id);
        return usuario;
    }

    @Override
    public boolean existeUsuario(String usuario) {
        String jpql = "SELECT COUNT(p) FROM Usuario p WHERE p.usuario = :usuario";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("usuario", usuario);

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Usuario findByContrasena(String contrasena) {
        String jpql = "SELECT p FROM Usuario p WHERE p.contrasena = :contrasena";
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
        query.setParameter("contrasena", contrasena);

        Usuario usuario = query.getSingleResult();
        return usuario;
    }

    @Override
    public Usuario findByUsuario(String usuario) {
        String jpql = "SELECT p FROM Usuario p WHERE p.usuario = :usuario";
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
        query.setParameter("usuario", usuario);

        Usuario usu = query.getSingleResult();
        return usu;
    }

    @Override
    public List<Usuario> listaUsuarios() {
        String jpql = "SELECT p FROM Usuario p";
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);

        return query.getResultList();
    }

}
