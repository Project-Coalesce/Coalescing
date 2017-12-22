package com.coalesce.coalescing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Updater extends Thread {

    private final Map<String, String> fileNames = new HashMap<>();

    private final String pluginFolder, updatesFolder;

    /*
    
    args[0] - the plugin folder
    args[1] - updates folder
    
    args[+] - the files. format: oldFile:newFile
    
     */
    
    private Updater(String pluginFolder, String updatesFolder, String[] files) {
        this.pluginFolder = pluginFolder;
        this.updatesFolder = updatesFolder;

        Stream.of(files).forEach(
                name -> {
                    String[] plugins = name.split(":");
                    fileNames.put(plugins[0]/*old*/, plugins[1]/*new*/);
                }
        );
        try {
            update();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update() throws Exception {
        File plugins = new File(pluginFolder);
        Stream.of(plugins.listFiles()).filter(file -> fileNames.keySet().contains(file.getName())).forEach(File::delete);
        
        File updates = new File(updatesFolder);
        Stream.of(updates.listFiles()).forEach(file -> {
            try {
                Files.move(Paths.get(file.getPath()), Paths.get(plugins.getPath(), file.getName()), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        
    }

    public static void main(String[] args) {
        new Updater(args[0], args[1], Arrays.copyOfRange(args, 2, args.length));
    }
}
