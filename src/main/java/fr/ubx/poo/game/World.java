/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.Entity;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final Map<Position, Entity> grid;
    private final WorldEntity[][] raw;
    public final Dimension dimension;
    private boolean changed = false; 

    public World(WorldEntity[][] raw) {
        this.raw = raw;
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
    }

    
    /** 
     * @return boolean
     */
    public boolean hasChanged() { 
        return changed; 
    }

    
    /** 
     * @param bool
     */
    public void setChanged(boolean bool) { 
        this.changed = bool; 
    }

    
    /** 
     * Cherche la position dans la grille de dépat (raw) la position du joueur.
     * 
     * @return Position Position du joueur
     * @throws PositionNotFoundException
     */
    public Position findPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Player) {
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Player");
    }
    
    
    /** 
     * Cherche la position dans la grille de départ (raw) la position de la porte, dépendant du paramètre
     * backing, qui cherche la porte suivante si backing est faux. La porte précédente s'il s'agit d'un retour.
     * 
     * @param backing
     * @return Position Position de la porte
     * @throws PositionNotFoundException
     */
    public Position findOpenedDoor(boolean backing) throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (((backing) && (raw[y][x] == WorldEntity.DoorNextClosed)) 
                || (((!backing) && (raw[y][x] == WorldEntity.DoorPrevOpened)))) {
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Door");
    }

    
    /** 
     * @param position
     * @return Decor
     */
    public Decor get(Position position) {
        return (Decor) grid.get(position);
    }

    
    /** 
     * @param position
     * @param decor
     */
    public void set(Position position, Decor decor) {
        grid.put(position, decor);
        changed = true;
    }

    
    /** 
     * @param position
     */
    public void clear(Position position) {
        grid.remove(position);
        changed = true;
    }

    
    /** 
     * @param fn
     */
    public void forEach(BiConsumer<Position, Entity> fn) {
        grid.forEach(fn);
    }

    
    /** 
     * @return Collection<Entity>
     */
    public Collection<Entity> values() {
        return grid.values();
    }

    
    /** 
     * Retourne vrai si la position est contenue dans les dimensions du monde, faux sinon.
     * 
     * @param position
     * @return boolean
     */
    public boolean isInside(Position position) {
        return position.inside(this.dimension);
    }

    
    /** 
     * Retourne vrai si la case contient un décor à la position donnée, faux sinon.
     * 
     * @param position Position de la case à vérifier
     * @return boolean
     */
    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }


    
    /** 
     * Cherche la position dans la grille de dépat (raw) la ou les positions des monstres.
     * 
     * @return List<Position> Liste des positions des monstres trouvés. La liste peut être nulle.
     */
    public List<Position> findMonster() {
        List<Position> pos = new ArrayList<>();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Monster) {
                    pos.add(new Position(x, y));
                }
            }
        }
        return pos;
    }

}
