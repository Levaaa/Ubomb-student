/*
package fr.ubx.poo.game;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import static fr.ubx.poo.game.WorldEntity.*;
public class WorldFromFile{
    private static final WorldEntity[][] mapEntities;
    private static int width;
    private static int height;

    public int worlddim(String name){
        try{
            file = new BufferedReader(new FileReader(name));
            int data;
            int x = 0;
            int y = 0;

            while ((data = file.read()) > -1) {
                char c = (char) data;
                if (c == '\n'){
                    x = 0;
                    y++;
                }
                x++;
            }

        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.width = x;
        this.height =y;
    }

    public WorldFromFile(String name){

        super(raw);

        /*
        String path = "src/main/resources/sample/";
        path = path.concat(name);
        try{
            BufferedReader file = new BufferedReader(new FileReader(path));
            int data;
            int x = 0;
            int y = 0;

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
                        this.mapEntities[x][y] = Stone;
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
        
    }    
}
*/