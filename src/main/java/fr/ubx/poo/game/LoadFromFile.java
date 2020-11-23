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
            this.height =y;
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }


    public LoadFromFile(int level, String path) {
        //charge le niveau depuis le fichier
        path = path.concat("/level" + String.valueOf(level) + ".txt");
        System.out.println(path);

        
        worldDim(level, path);
        System.out.println("w = " + width + " h = " + height);
        mapEntities = new WorldEntity[width][height];


        try{
            BufferedReader file = new BufferedReader(new FileReader(path));
            int data;

            for(int y = 0; y < height; y++){
                for(int x = 0; x < width + 1; x++){
                    data = file.read();
                    char c = (char) data;
                    System.out.print(c);
                    switch(c){
                        case '\n' :
                            break;
                        case '_' :
                            this.mapEntities[x][y] = Empty;
                            break;
                        case 'B' :
                            this.mapEntities[x][y] = Box;
                            break;
                        case 'H' :
                            this.mapEntities[x][y] = Heart;
                            break;
                        case 'K' :
                            this.mapEntities[x][y] = Key;
                            break;
                        case 'M' :
                            this.mapEntities[x][y] = Monster;
                            break;
                        case 'V':
                            this.mapEntities[x][y] = DoorPrevOpened;
                            break;
                        case 'N':
                            this.mapEntities[x][y] = DoorNextOpened;
                            break;
                        case 'n':
                            this.mapEntities[x][y] = DoorNextClosed;
                            break;
                        case 'P':
                            this.mapEntities[x][y] = Player;
                            break;
                        case 'S':
                            this.mapEntities[x][y] = Stone;
                            break;
                        case 'T':
                            this.mapEntities[x][y] = Tree;
                            break;
                        case 'W':
                            this.mapEntities[x][y] = Princess;
                            break;
                        case '>':
                            this.mapEntities[x][y] = BombRangeInc;
                            break;
                        case '<':
                            this.mapEntities[x][y] = BombRangeDec;
                            break;
                        case '+':
                            this.mapEntities[x][y] = BombNumberInc;
                            break;
                        case '-':
                            this.mapEntities[x][y] = BombNumberDec;
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
