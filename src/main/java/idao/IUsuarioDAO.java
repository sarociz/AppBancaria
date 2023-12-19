package idao;

import models.Usuario;

import java.util.List;

public interface IUsuarioDAO {

    boolean existeUsuario(String usuario);

    Usuario findByContrasena(String contrasena);

    Usuario findByUsuario(String usuario);

    List<Usuario> listaUsuarios();
}
