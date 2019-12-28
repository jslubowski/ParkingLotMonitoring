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

    public static Mat drawParkingSpace(ParkingSpace parkingSpace, Mat sourceImage, boolean occupation) {
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

   public static List<MatOfPoint> findContours(Mat imageProcessed){
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(imageProcessed, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    public static List<Rect> findRectangles(Mat imageProcessed, List<MatOfPoint> contours, String name, double area){
        List<Rect> retContours = new ArrayList<>();
        Mat rectangles = Mat.zeros(imageProcessed.size(), CvType.CV_8UC3);
        for(MatOfPoint c: contours) {
            Rect rect = Imgproc.boundingRect(c);
            if((rect.area() > (area / 6)) || (name == "P1" && rect.area() > area / 3) ) {
                Imgproc.rectangle(rectangles, rect, new Scalar(0, 255, 0), 1);
                retContours.add(rect);
            }
        }
        return retContours;
    }

    public static List<Rect> getRectangles(ParkingSpace parkingSpace){
        String name = parkingSpace.getParkingSpaceName();
        double area = parkingSpace.getArea();
        Mat imageProcessed = parkingSpace.getImageProcessed();
        List<MatOfPoint> contours = SpacesDrawer.findContours(imageProcessed);
        return SpacesDrawer.findRectangles(imageProcessed, contours, name, area);
    }

}
