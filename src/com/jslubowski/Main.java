package com.jslubowski;

import com.jslubowski.dip.Processing;
import com.jslubowski.model.ParkingSpace;
import com.jslubowski.util.MyFileReader;
import com.jslubowski.util.ParkingSpacesCreator;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO transfer whole app to the maven

public class Main {

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);                                                                       // library load, needed for OpenCV
        String projectFilePath = System.getProperty("user.dir") + "\\src\\data\\";                                          // gets current path
        MyFileReader myFileReader = new MyFileReader(projectFilePath);
        String[] batches = myFileReader.getAllFoldersFromDirectory();
        List<ParkingSpace> previousParkingSpaces = new ArrayList<>();

        for(String batch : batches) {                                                                                           //for photo batch folder
            System.out.println("Batch folder: " + batch);
            List<String> imagesPaths;
            try {
                imagesPaths = myFileReader.readAllFilesInDirectory(batch);
            }catch (IOException e){
                continue;
            }

            List<Integer> spacesCoordinates = new ArrayList<>();
            if (imagesPaths.size() != 0) {
                try {
                    spacesCoordinates = myFileReader.findAllSpaceCoordinates(batch);
                }catch (IOException e){
                    System.out.println("Couldn't read spaces.txt");
                    continue;
                }
            }

            for(String imagePath: imagesPaths) {
                if(imagesPaths.size() == 0) break;                                                                      // if no files in a dir then exit
                String[] arrImagePath = imagePath.split("\\\\");
                String filename = arrImagePath[arrImagePath.length - 1].split("\\.")[0];
                if(arrImagePath[arrImagePath.length - 1].split("\\.")[1].equals("jpg")) {                        // check if a photo

                    // Main method steps
                    // 1. Load an image
                    Mat sourceImage = Imgcodecs.imread(imagePath);
                    Mat outputImage = sourceImage.clone();

                    // 2. Initialize ParkingSpaces points on an image
                    List<ParkingSpace> currentParkingSpaces =
                            ParkingSpacesCreator.createParkingSpaces(spacesCoordinates, sourceImage);

                    // 3. Preprocess all parking spaces and add to currentParkingSpaces list
                    currentParkingSpaces = Processing.preProcessAll(currentParkingSpaces);

                    // 4. Run occupation verification
                    Processing.runAnalysis(previousParkingSpaces, currentParkingSpaces,
                            outputImage, projectFilePath, filename, batch);

                    // 5. Save current parking spaces as previous and go to next iteration
                    previousParkingSpaces = new ArrayList<>();
                    for(ParkingSpace space: currentParkingSpaces){
                        previousParkingSpaces.add(space);
                    }
                }
            }
        }
    }
}
