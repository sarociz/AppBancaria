package idao;

import models.Usuario;

public interface IUsuarioDAO {

    boolean existeUsuario(String usuario);

    Usuario findByContrasena(String contrasena);

    Usuario findByUsuario(String usuario);
}
