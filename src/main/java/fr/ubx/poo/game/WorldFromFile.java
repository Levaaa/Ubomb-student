package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import static fr.ubx.poo.game.WorldEntity.*;

public class WorldFromFile extends World{

    public WorldFromFile(WorldEntity[][] mapEntities){
        super(mapEntities);
    }    
}
