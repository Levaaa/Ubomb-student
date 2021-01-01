package fr.ubx.poo.game;

import fr.ubx.poo.model.Entity;
import fr.ubx.poo.model.decor.*;

import java.util.Hashtable;
import java.util.Map;

public class WorldBuilder {
    private final Map<Position, Entity> grid = new Hashtable<>();

    private WorldBuilder() {
    }

    
    /** 
     * Fabrique une Map de touts les éléments de décors de la grille de départ avec pour clé la positions de ces dernières.
     * 
     * @param raw Grille de départ.
     * @param dimension Dimmensions de la grille.
     * @return Map<Position, Entity> Map créée.
     */
    public static Map<Position, Entity> build(WorldEntity[][] raw, Dimension dimension) {
        WorldBuilder builder = new WorldBuilder();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                Position pos = new Position(x, y);
                Decor decor = processEntity(raw[y][x]);
                if (decor != null)
                    builder.grid.put(pos, decor);
            }
        }
        return builder.grid;
    }

    
    /** 
     * Retourne un nouvel objet Decor en fonction de son équivalent en WorldEntity.
     * 
     * @param entity L'élément à convertir
     * @return Decor Nouveau décor créé
     */
    private static Decor processEntity(WorldEntity entity) {
        switch (entity) {
            case Stone:
                return new Stone();
            case Tree:
                return new Tree();
            case Box:
                return new Box();
            case BombNumberDec:
                return new BombNbDec();
            case BombNumberInc:
                return new BombNbInc();
            case BombRangeDec:
                return new BombRangeDec();
            case BombRangeInc:
                return new BombRangeInc();
            case DoorNextClosed:
                return new DoorNextClosed();
            case DoorNextOpened:
                return new DoorNextOpened();
            case DoorPrevOpened:
                return new DoorPrevOpened();
            case Princess:
                return new Princess();
            case Key:
                return new Key();
            case Heart:
                return new Heart();
            default:
                return null;
        }
    }
}
