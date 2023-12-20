package ClienteServidor;

public class ExpresionesRegulares {

    public static boolean comprobarContrasena(String contrasena) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        return contrasena.matches(regex);
    }

    public static boolean comprobarCorreo(String correo){
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return correo.matches(regex);
    }

    public static boolean comprobarNumCuenta(String nimcuenta) {
        // Patrón de expresión regular para verificar números de cuenta(12 dígitos)
        String regex = "^[0-9]{12}$";

        return nimcuenta.matches(regex);
    }
}
