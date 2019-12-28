package com.jslubowski.model;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.List;

// TODO Lombok
public class ParkingSpace {

    private String parkingSpaceName ;
    private Point cornerTopLeft;
    private Point cornerBottomRight;
    private Mat image;
    private Mat imageProcessed;
    private double area;
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
        // Are conditions met?
        return rectangles.size() >= 1;
    }


    public Point getCornerTopLeft() {
        return cornerTopLeft;
    }

    public void setCornerTopLeft(Point cornerTopLeft) {
        this.cornerTopLeft = cornerTopLeft;
    }

    public Point getCornerBottomRight() {
        return cornerBottomRight;
    }

    public void setCornerBottomRight(Point cornerBottomRight) {
        this.cornerBottomRight = cornerBottomRight;
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    public String getParkingSpaceName() {
        return parkingSpaceName;
    }

    public void setParkingSpaceName(String parkingSpaceName) {
        this.parkingSpaceName = parkingSpaceName;
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

    public void setArea(int area) {
        this.area = area;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
