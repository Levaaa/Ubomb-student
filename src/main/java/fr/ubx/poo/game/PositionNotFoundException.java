package fr.ubx.poo.game;

/**
 * Génère une exeption si la posision recherché n'a pas été trouvée.
 */
public class PositionNotFoundException extends Exception {
    public PositionNotFoundException(String message) {
        super(message);
    }
}
