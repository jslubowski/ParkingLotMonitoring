package com.jslubowski.util;

import com.jslubowski.model.ParkingSpace;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class ParkingSpacesCreator {

    public static List<ParkingSpace> createParkingSpaces(List<Integer> spacesCoordinates, Mat sourceImage){
        List<ParkingSpace> parkingSpaces = new ArrayList<>();
        int parkingSpace = 1;

        for (int i = 0; i < spacesCoordinates.size(); i = i + 4) {
            Point cornerTopLeft = new Point(spacesCoordinates.get(i), spacesCoordinates.get(i + 1));
            Point cornerBottomRight = new Point(spacesCoordinates.get(i + 2), spacesCoordinates.get(i + 3));
            ParkingSpace p = new ParkingSpace(cornerTopLeft, cornerBottomRight, sourceImage, "P" + parkingSpace);
            parkingSpaces.add(p);
            parkingSpace++;
        }
        return parkingSpaces;
    }
}
