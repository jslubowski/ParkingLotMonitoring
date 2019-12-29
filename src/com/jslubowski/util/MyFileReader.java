package com.jslubowski.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyFileReader {
    private final static String FILENAME = "\\spaces.txt";

    private final String projectFilePath;

    public MyFileReader(String projectFilePath) {
        this.projectFilePath = projectFilePath + "input\\";
    }

    public List<String> readAllFilesInDirectory(String batchFolder) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(this.projectFilePath + batchFolder)))
        {
            List<String> imagesPaths = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
            return imagesPaths;
        }
        catch (IOException e) {
            System.out.println("Error while reading " + batchFolder + " folder.");
            throw e;
        }
    }

    public List<Integer> findAllSpaceCoordinates(String batchFolder) throws IOException {
        List<Integer> spacesCoordinates = new ArrayList<>();
        try (FileInputStream inputTextFile = new FileInputStream(this.projectFilePath + batchFolder + FILENAME);
             Scanner scanner = new Scanner(inputTextFile))
        {
            while (scanner.hasNextInt()) {
                spacesCoordinates.add(scanner.nextInt());
            }
        }
        catch (IOException e) {
            System.out.println("IOException :" + e.getMessage());
            throw e;
        }
        return spacesCoordinates;
    }

    public String[] getAllFoldersFromDirectory() {
        File file = new File(this.projectFilePath);
        String[] batches = file.list((File dir, String name)-> new File(dir, name).isDirectory());
        return batches;
    }
}
