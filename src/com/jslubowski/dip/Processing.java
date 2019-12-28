package com.jslubowski.dip;

import com.jslubowski.model.ParkingSpace;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Processing {

    private final static int THRESHOLD1                         = 200;
    private final static int THRESHOLD2                         = 300;
    private final static int KERNEL_SIZE                        = 3;
    private final static int DILATION_ITERATIONS                = 4;
    private final static int EROSION_ITERATIONS                 = 1;
    private final static Point MORPHOLOGY_POINT                 = new Point(-1, -1);
    private final static int WHITE_PIXEL_DIFFERENCE_THRESHOLD   = 450;


    public static Mat preProcess(String name, Mat image) {
        Mat edges = new Mat();
        Mat imageProcessed = new Mat(image.rows(), image.cols(), image.type());
        Mat morphologyKernel = Mat.ones(3, 3, 1);

        // Algorithm steps
        Imgproc.GaussianBlur(image, imageProcessed, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);                     // 1. Gaussian blur
        Imgproc.Canny(imageProcessed, edges, THRESHOLD1, THRESHOLD2, KERNEL_SIZE, false);                                               // 2. Canny Edge detection
        imageProcessed = edges;
        Imgproc.dilate(imageProcessed, imageProcessed, morphologyKernel, MORPHOLOGY_POINT, DILATION_ITERATIONS);                                     // 3. Dilation
        Imgproc.erode(imageProcessed, imageProcessed, morphologyKernel, MORPHOLOGY_POINT, EROSION_ITERATIONS);                                      // 4. Erosion
        return imageProcessed;
    }

    public static int getWhitePixels(Mat image){
        int whitePixels = 0;
        for(int row = 0; row < image.rows(); row++){
            for(int column = 0; column < image.cols(); column++){
                if(image.get(row,column)[0] == 255.0) whitePixels++;
            }
        }
        return whitePixels;
    }

    public static List<ParkingSpace> preProcessAll(List<ParkingSpace> parkingSpaces){
        List<ParkingSpace> currentParkingSpaces = new ArrayList<>();
        for(ParkingSpace space: parkingSpaces){
            String name = space.getName();
            Mat image = space.getImage();
            Mat processedImage = Processing.preProcess(name, image);
            space.setImageProcessed(processedImage);
            currentParkingSpaces.add(space);
        }
        return currentParkingSpaces;
    }

    public static int getWhitePixelsDifference(Mat first, Mat second){
        int currentWhitePixels = Processing.getWhitePixels(first);
        int previousWhitePixels = Processing.getWhitePixels(second);
        return Math.abs(currentWhitePixels - previousWhitePixels);
    }

    public static void runAnalysis(List<ParkingSpace> previousParkingSpaces,
                                   List<ParkingSpace> currentParkingSpaces, Mat outputImage,
                                   String projectFilePath, String filename, String s){

        if(!previousParkingSpaces.isEmpty()){
            for(int i = 0; i < currentParkingSpaces.size(); i++) {

                // 1. Calculate the difference of white pixels between two images
                int difference = Processing.getWhitePixelsDifference(currentParkingSpaces.get(i).getImageProcessed(),
                        previousParkingSpaces.get(i).getImageProcessed());

                /*
                 2. If number of pixels isn't bigger then THRESHOLD then check the contours
                    Otherwise set occupation as the same value as a previous photo
                 */
                if(difference > WHITE_PIXEL_DIFFERENCE_THRESHOLD){
                    List<Rect> rectangles = Drawing.getRectangles(currentParkingSpaces.get(i));
                    currentParkingSpaces.get(i).setOccupied(currentParkingSpaces.get(i).checkOccupation(rectangles));
                    outputImage = Drawing.drawParkingSpace(currentParkingSpaces.get(i), outputImage, currentParkingSpaces.get(i).isOccupied());
                } else {
                    currentParkingSpaces.get(i).setOccupied(previousParkingSpaces.get(i).isOccupied());
                    outputImage = Drawing.drawParkingSpace(currentParkingSpaces.get(i), outputImage, currentParkingSpaces.get(i).isOccupied());
                }
            }
            Imgcodecs.imwrite(projectFilePath + "output\\" + s + "\\" + filename + "_analysed" + ".jpg", outputImage);
        }
        // 3. If it is the first image
        else {
            for (ParkingSpace p : currentParkingSpaces) {
                List<Rect> rectangles = Drawing.getRectangles(p);
                p.setOccupied(p.checkOccupation(rectangles));
                outputImage = Drawing.drawParkingSpace(p, outputImage, p.isOccupied());
            }
            Imgcodecs.imwrite(projectFilePath + "output\\" + s + "\\" + filename + "_analysed" + ".jpg", outputImage);
        }
    }

}
