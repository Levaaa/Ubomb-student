package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static fr.ubx.poo.game.WorldEntity.*;
public class WorldFromFile extends World {
    private static final WorldEntity[][] mapEntities;

    public WorldFromFile(String name){
        String path = "src/main/resources/sample/";
        path = path.concat(name);
        try{            
            BufferedReader file = new BufferedReader(new FileReader(path));
            int data;
            int x, y = 0;

            while ((data = file.read()) > -1) {
                char c = (char) data;
                switch(c){
                    case '\n' : 
                        x = 0;
                        y++;
                        break;
                    case '_' :    
                    case 'B' :
                    case 'H' :
                    case 'K' :
                    case 'M' :
                    case 'V' :
                    case 'N' :
                    case 'n' :
                    case 'P' :
                    case 'S' :
                    case 'T' :
                    case 'W' :
                    case '>' :
                    case '<' :
                    case '+' :
                    case '-' :
                        this.mapEntities[x][y] = ;
                        break;

                    default :
                        break;
                }
                x++;
            }   
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
