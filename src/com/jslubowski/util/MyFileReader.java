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

    private String projectFilePath;

    public MyFileReader(String projectFilePath) {
        this.projectFilePath = projectFilePath + "input\\";
    }

    public List<String> readAllFilesInDirectory(String batchFolder) throws IOException {
        List<String> imagesPaths;
        try (Stream<Path> walk = Files.walk(Paths.get(this.projectFilePath + batchFolder))) {
            imagesPaths = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error while reading " + batchFolder + " folder.");
            throw e;
        }
        return imagesPaths;
    }

    public List<Integer> findAllSpaceCoordinates(String batchFolder) throws IOException {
        List<Integer> spacesCoordinates = new ArrayList<>();
        try (FileInputStream inputTextFile = new FileInputStream(this.projectFilePath + batchFolder + "\\spaces.txt");
            Scanner scanner = new Scanner(inputTextFile)) {
            while (scanner.hasNextInt()) {
                spacesCoordinates.add(scanner.nextInt());
            }
        } catch (IOException e) {
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

    public String getProjectFilePath() {
        return projectFilePath;
    }

    public void setProjectFilePath(String projectFilePath) {
        this.projectFilePath = projectFilePath;
    }
}
