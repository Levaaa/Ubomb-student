package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import fr.ubx.poo.model.decor.*;
import static fr.ubx.poo.game.WorldEntity.*;
import fr.ubx.poo.game.WorldEntity;

import java.io.FileNotFoundException;

public class LoadFromFile {
    /**
     * Tableau de la carte à charger.
     */
    private WorldEntity[][] mapEntities;
    
    /**
     * Largeur du niveau.
     */
    private int width;
    
    /**
     * Hauteur du niveau.
     */
    private int height;

    /** 
     * Calcul les dimensions d'un niveau à partir du fichier correspondant au path donné
     * 
     * @param path
     */
    public void worldDim(String path){
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

    /**
     * Génère le tableau de WorldEntity à partir du fichier correspondant au path donné.
     * On parcourt le fichier caractère par caractère et met à jour le tableau de la classe au fur et à mesure. 
     * 
     * @param level Numéro du niveau
     * @param path Chemin du fichier
     * @param prefix Préfixe du fichier du niveau 
     */
    public LoadFromFile(int level, String path, String prefix) {
        path = path.concat("/"+ prefix + String.valueOf(level) + ".txt");

        worldDim(path);
        mapEntities = new WorldEntity[height][width];

        try{
            BufferedReader file = new BufferedReader(new FileReader(path));
            int data;
            
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width + 1; x++){
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
                        case 'X':
                            this.mapEntities[y][x] = Malediction;
                        default :
                            break;
                        }
                }
            } 
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * @return WorldEntity[][]
     */
    public WorldEntity[][] getMapEntities() {
        return this.mapEntities;
    }    
}
