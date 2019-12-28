package com.jslubowski.dip;

import com.jslubowski.model.ParkingSpace;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Drawing {

    public static Mat drawParkingSpace(ParkingSpace p, Mat sourceImage, boolean occupation) {
        Scalar color;
        if (occupation) {
            color = new Scalar(0, 0, 255);
        } else {
            color = new Scalar(0, 255, 0);
        }
        Imgproc.rectangle(sourceImage, new Rect(p.getCornerTopLeft(), p.getCornerBottomRight()), color, 1);
        double xTextPoint = p.getCornerTopLeft().x;
        double yTextPoint = p.getCornerTopLeft().y - 10;
        Imgproc.putText(sourceImage, p.getName(), new Point(xTextPoint, yTextPoint), Imgproc.FONT_HERSHEY_PLAIN, 1, color, 1);

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
        String name = parkingSpace.getName();
        double area = parkingSpace.getArea();
        Mat imageProcessed = parkingSpace.getImageProcessed();
        List<MatOfPoint> contours = Drawing.findContours(imageProcessed);
        return Drawing.findRectangles(imageProcessed, contours, name, area);
    }

}
