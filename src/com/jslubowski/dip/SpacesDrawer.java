package com.jslubowski.dip;

import com.jslubowski.model.ParkingSpace;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class SpacesDrawer {

    private final static Scalar RED                 = new Scalar(0, 0, 255);
    private final static Scalar GREEN               = new Scalar(0, 255, 0);
    private final static int RECTANGLE_THICKNESS    = 1;
    private final static int TEXT_THICKNESS         = 1;
    private final static int TEXT_Y_OFFSET          = 10;
    private final static int TEXT_FONT_SCALE        = 1;

    private final ParkingSpace parkingSpace;
    private final List<MatOfPoint> contours;
    private final List<Rect> rectangles;

    public SpacesDrawer(ParkingSpace parkingSpace) {
        this.parkingSpace = parkingSpace;
        this.contours = findContours(parkingSpace.getImageProcessed());
        this.rectangles = findRectangles(parkingSpace.getImageProcessed(), parkingSpace.getParkingSpaceName(), parkingSpace.getArea());
    }

    public Mat drawParkingSpace(Mat sourceImage, boolean occupation) {
        Scalar color;
        if (occupation) {
            color = RED;
        } else {
            color = GREEN;
        }
        Imgproc.rectangle(sourceImage, new Rect(parkingSpace.getCornerTopLeft(), parkingSpace.getCornerBottomRight()), color, RECTANGLE_THICKNESS);
        double xTextPoint = parkingSpace.getCornerTopLeft().x;
        double yTextPoint = parkingSpace.getCornerTopLeft().y - TEXT_Y_OFFSET;
        Point textPoint = new Point(xTextPoint, yTextPoint);
        Imgproc.putText(sourceImage, parkingSpace.getParkingSpaceName(), textPoint, Imgproc.FONT_HERSHEY_PLAIN, TEXT_FONT_SCALE, color, TEXT_THICKNESS);

        return sourceImage;
    }

   private List<MatOfPoint> findContours(Mat imageProcessed){
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(imageProcessed, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private List<Rect> findRectangles(Mat imageProcessed, String name, double area){
        List<Rect> retContours = new ArrayList<>();
        Mat rectangles = Mat.zeros(imageProcessed.size(), CvType.CV_8UC3);
        for(MatOfPoint c: contours) {
            Rect parkingSpaceRect = Imgproc.boundingRect(c);
            if(isSpaceOccupied(area, name, parkingSpaceRect)) {
                Imgproc.rectangle(rectangles, parkingSpaceRect, GREEN, RECTANGLE_THICKNESS);
                retContours.add(parkingSpaceRect);
            }
        }
        return retContours;
    }

    public List<Rect> getRectangles(){
        return this.rectangles;
    }

    private boolean isSpaceOccupied(double area, String name, Rect parkingSpaceRect){
        return (parkingSpaceRect.area() > (area / 6)) || (name.equals("P1") && (parkingSpaceRect.area() > area / 3));
    }
}
