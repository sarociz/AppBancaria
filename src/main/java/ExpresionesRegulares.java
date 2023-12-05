public class ExpresionesRegulares {
    public static boolean comprobarContrasena(String contrasena) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        return contrasena.matches(regex);
    }
    public static boolean comprobarUsuario(String usuario) {
        String regex = "";
        return usuario.matches(regex);
    }
}
