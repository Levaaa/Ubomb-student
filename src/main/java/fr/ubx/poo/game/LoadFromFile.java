package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import fr.ubx.poo.model.decor.*;
import static fr.ubx.poo.game.WorldEntity.*;

import java.io.FileNotFoundException;

public class LoadFromFile {
    private WorldEntity[][] mapEntities;
    private int width;
    private int height;

    public void worldDim(int level, String path){
        try{
            BufferedReader file = new BufferedReader(new FileReader(path));
            int data;
            int x = 0;
            int y = 0;

            while ((data = file.read()) > -1) {
                char c = (char) data;
                if (c == '\n'){
                    y++;
                }
                if (y == 0) x++;
            }
            this.width = x;
            this.height = y;
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }


    public LoadFromFile(int level, String path, String prefix) {
        //charge le niveau depuis le fichier
        path = path.concat("/"+ prefix + String.valueOf(level) + ".txt");
        System.out.println(path);

        
        worldDim(level, path);
        System.out.println("w = " + width + " h = " + height);
        mapEntities = new WorldEntity[width][height];

        try{
            BufferedReader file = new BufferedReader(new FileReader(path));
            int data;

            for(int x = 0; x < height; x++){
                for(int y = 0; y < width + 1; y++){
                    data = file.read();
                    char c = (char) data;
                    switch(c){
                        case '\n' :
                            break;
                        case '_' :
                            this.mapEntities[y][x] = Empty;
                            break;
                        case 'B' :
                            this.mapEntities[y][x] = Box;
                            break;
                        case 'H' :
                            this.mapEntities[y][x] = Heart;
                            break;
                        case 'K' :
                            this.mapEntities[y][x] = Key;
                            break;
                        case 'M' :
                            this.mapEntities[y][x] = Monster;
                            break;
                        case 'V':
                            this.mapEntities[y][x] = DoorPrevOpened;
                            break;
                        case 'N':
                            this.mapEntities[y][x] = DoorNextOpened;
                            break;
                        case 'n':
                            this.mapEntities[y][x] = DoorNextClosed;
                            break;
                        case 'P':
                            this.mapEntities[y][x] = Player;
                            break;
                        case 'S':
                            this.mapEntities[y][x] = Stone;
                            break;
                        case 'T':
                            this.mapEntities[y][x] = Tree;
                            break;
                        case 'W':
                            this.mapEntities[y][x] = Princess;
                            break;
                        case '>':
                            this.mapEntities[y][x] = BombRangeInc;
                            break;
                        case '<':
                            this.mapEntities[y][x] = BombRangeDec;
                            break;
                        case '+':
                            this.mapEntities[y][x] = BombNumberInc;
                            break;
                        case '-':
                            this.mapEntities[y][x] = BombNumberDec;
                            break;
                        default :
                            break;

                    }
                }
            } 
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WorldEntity[][] getMapEntities() {
        return this.mapEntities;
    }    
}
