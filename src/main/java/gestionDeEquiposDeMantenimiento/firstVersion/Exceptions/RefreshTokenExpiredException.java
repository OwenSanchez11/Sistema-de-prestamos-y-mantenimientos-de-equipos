package gestionDeEquiposDeMantenimiento.firstVersion.Exceptions;

public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
