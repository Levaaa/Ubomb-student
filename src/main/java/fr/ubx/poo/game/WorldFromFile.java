package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FILE;
import java.io.FileWriter;

import static fr.ubx.poo.game.WorldEntity.*;
public class WorldFromFile extends World {
    private static final WorldEntity[][];

    public WorldFromFile(String name) {
        String path = "../../ressources/sample/"
        path = path.concat(name);
        try{
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileWriter(file.getAbsoluteFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Lise le fichier
        //construit WorldEntity
        //super(WorldEntity);
    }

    public WorldFromFile() {
        //charger WorldStatic
    }

    
}
