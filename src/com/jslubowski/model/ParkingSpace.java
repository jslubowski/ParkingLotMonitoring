package com.jslubowski.model;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.List;

// TODO Lombok
public class ParkingSpace {

    private final String parkingSpaceName ;
    private final Point cornerTopLeft;
    private final Point cornerBottomRight;
    private final Mat image;
    private Mat imageProcessed;
    private final double area;
    private boolean occupied;

    public ParkingSpace(Point cornerTopLeft, Point cornerBottomRight, Mat image, String parkingSpaceName) {
        this.cornerTopLeft = cornerTopLeft;
        this.cornerBottomRight = cornerBottomRight;
        this.parkingSpaceName = parkingSpaceName;
        this.area = (cornerBottomRight.x - cornerTopLeft.x) * (cornerBottomRight.y - cornerTopLeft.y);
        Rect rectCrop = new Rect(cornerTopLeft, cornerBottomRight);
        this.image = new Mat(image, rectCrop);
        this.imageProcessed = this.image;
    }

    public boolean checkOccupation(List<Rect> rectangles){
        return rectangles.size() >= 1;
    }


    public Point getCornerTopLeft() {
        return cornerTopLeft;
    }

    public Point getCornerBottomRight() {
        return cornerBottomRight;
    }

    public Mat getImage() {
        return image;
    }

    public String getParkingSpaceName() {
        return parkingSpaceName;
    }

    public Mat getImageProcessed() {
        return imageProcessed;
    }

    public void setImageProcessed(Mat imageProcessed) {
        this.imageProcessed = imageProcessed;
    }

    public double getArea() {
        return area;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
