package xyz.rodeldev;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystem {
    private File dataFolder, overrideFile, menusFolder;

    public FileSystem(File dataFolder){
        try {
            this.dataFolder = dataFolder;
            if(!this.dataFolder.exists()){
                this.dataFolder.mkdirs();
            }
    
            overrideFile = new File(this.dataFolder, "override.json");
            if(!overrideFile.exists()){
                overrideFile.createNewFile();
            }

            menusFolder = new File(this.dataFolder, "menus");
            if(!menusFolder.exists()){
                menusFolder.mkdirs();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public File getDataFolder(){
        return dataFolder;
    }

    public File getOverrideFile(){
        return overrideFile;
    }

    public File getMenusFolder(){
        return menusFolder;
    }

    public List<String> listMenuNames(){
        return Arrays.asList(getMenusFolder().list()).stream().filter(name -> name.endsWith(".json")).map(name -> name.replace(".json", "")).collect(Collectors.toList());
    }

    public File getMenu(String name){
        return new File(this.menusFolder, name+".json");
    }

    public boolean saveMenu(String name, String data){
        try {
            File file = getMenu(name);
            if(!file.exists()){
                file.createNewFile();
            }
            try(FileOutputStream outputStream = new FileOutputStream(file)){
                outputStream.write(data.getBytes());
                return true;
            } catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}