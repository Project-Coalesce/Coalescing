package com.coalesce.coalescing;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class Updater {

    private final Map<String, String> fileNames = new HashMap<>();

    private final String pluginFolder, updatesFolder;

    private Updater(String pluginFolder, String updatesFolder, String[] files) {
        this.pluginFolder = pluginFolder;
        this.updatesFolder = updatesFolder;

        Stream.of(files).forEach(
                name -> {
                    String[] plugins = name.split(":");
                    fileNames.put(plugins[0], plugins[1]);
                }
        );
        update();
    }

    private void update() {
        File plugins = new File(pluginFolder);
        Stream.of(plugins.listFiles()).filter(file -> fileNames.keySet().contains(file.getName())).forEach(File::delete);

        File updates = new File(updatesFolder);
        Stream.of(updates.listFiles()).forEach(file -> {
            try {
                Files.move(Paths.get(file.getPath()), Paths.get(plugins.getPath(), file.getName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {}
        });
    }

    public static void main(String[] args) {
        new Updater(args[0], args[1], Arrays.copyOfRange(args, 2, args.length));
    }
}
