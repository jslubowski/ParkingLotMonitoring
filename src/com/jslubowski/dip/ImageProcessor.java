package com.jslubowski.dip;

import com.jslubowski.model.ParkingSpace;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class ImageProcessor {

    private final static int THRESHOLD1                         = 200;
    private final static int THRESHOLD2                         = 300;
    private final static int KERNEL_SIZE                        = 3;
    private final static int DILATION_ITERATIONS                = 4;
    private final static int EROSION_ITERATIONS                 = 1;
    private final static Point MORPHOLOGY_POINT                 = new Point(-1, -1);
    private final static int WHITE_PIXEL_DIFFERENCE_THRESHOLD   = 450;
    private final static int FIRST_IMAGE_CHANNEL                = 0;
    private final static boolean USE_L2_GRADIENT                = false;

    private final List<ParkingSpace> currentParkingSpaces;
    private final List<ParkingSpace> previousParkingSpaces;
    private Mat outputImage;

    public ImageProcessor(List<ParkingSpace> currentParkingSpaces, List<ParkingSpace> previousParkingSpaces, Mat outputImage) {
        this.currentParkingSpaces = currentParkingSpaces;
        this.previousParkingSpaces = previousParkingSpaces;
        this.outputImage = outputImage;
    }

    public int getWhitePixels(Mat image){
        int whitePixels = 0;
        for(int row = 0; row < image.rows(); row++){
            for(int column = 0; column < image.cols(); column++){
                if(isPixelWhite(image.get(row, column))) {
                    whitePixels++;
                }
            }
        }
        return whitePixels;
    }

    private Mat preProcess(Mat image) {
        Mat edges = new Mat();
        Mat imageProcessed = new Mat(image.rows(), image.cols(), image.type());
        Mat morphologyKernel = Mat.ones(3, 3, 1);

        Imgproc.GaussianBlur(image, imageProcessed, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
        Imgproc.Canny(imageProcessed, edges, THRESHOLD1, THRESHOLD2, KERNEL_SIZE, USE_L2_GRADIENT);
        Imgproc.dilate(edges, edges, morphologyKernel, MORPHOLOGY_POINT, DILATION_ITERATIONS);
        Imgproc.erode(edges, edges, morphologyKernel, MORPHOLOGY_POINT, EROSION_ITERATIONS);
        return edges;
    }

    public void preProcessAllCurrentSpaces(){
        this.currentParkingSpaces.forEach(space -> {
            space.setImageProcessed(preProcess(space.getImage()));
        });
    }

    private int getWhitePixelsDifference(Mat first, Mat second){
        int currentWhitePixels = getWhitePixels(first);
        int previousWhitePixels = getWhitePixels(second);
        return Math.abs(currentWhitePixels - previousWhitePixels);
    }

    public void runAnalysis(String projectFilePath, String filename, String batchFolder){
        if(!this.previousParkingSpaces.isEmpty()){
            processPhoto();
            Imgcodecs.imwrite(projectFilePath + "output\\" + batchFolder + "\\" + filename + "_analysed" + ".jpg", this.outputImage);
        }
        else {
            processFirstPhoto();
            Imgcodecs.imwrite(projectFilePath + "output\\" + batchFolder + "\\" + filename + "_analysed" + ".jpg", outputImage);
        }
    }

    private boolean isPixelWhite(double[] pixel){
        return pixel[FIRST_IMAGE_CHANNEL] == 255.0;
    }

    private void processPhoto(){
        for(int i = 0; i < this.currentParkingSpaces.size(); i++) {
            ParkingSpace currentSpace = this.currentParkingSpaces.get(i);
            ParkingSpace previousSpace = this.previousParkingSpaces.get(i);
            SpacesDrawer spacesDrawer = new SpacesDrawer(currentSpace);
            int whitePixelsDifference = getWhitePixelsDifference(currentSpace.getImageProcessed(), previousSpace.getImageProcessed());

            if(whitePixelsDifference > WHITE_PIXEL_DIFFERENCE_THRESHOLD){
                List<Rect> rectangles = spacesDrawer.getRectangles();
                currentSpace.setOccupied(currentSpace.checkOccupation(rectangles));
                this.outputImage = spacesDrawer.drawParkingSpace(this.outputImage, currentSpace.isOccupied());
            } else {
                currentSpace.setOccupied(previousSpace.isOccupied());
                this.outputImage = spacesDrawer.drawParkingSpace(this.outputImage, currentSpace.isOccupied());
            }
        }
    }

    private void processFirstPhoto(){
        for (ParkingSpace parkingSpace : this.currentParkingSpaces) {
            SpacesDrawer spacesDrawer = new SpacesDrawer(parkingSpace);
            List<Rect> rectangles = spacesDrawer.getRectangles();
            parkingSpace.setOccupied(parkingSpace.checkOccupation(rectangles));
            this.outputImage = spacesDrawer.drawParkingSpace(this.outputImage, parkingSpace.isOccupied());
        }
    }
}

