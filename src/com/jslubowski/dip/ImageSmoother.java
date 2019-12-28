package com.jslubowski.dip;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

// TODO
public class ImageSmoother {

    private static final int KERNEL_SIZE = 3;

    public Mat smoothGrayscaleImage(Mat source){
        Mat kernel = Mat.ones(KERNEL_SIZE, KERNEL_SIZE, CvType.CV_32F);
        for(int i = 0; i < kernel.rows(); i++) {
            for (int j = 0; j < kernel.cols(); j++) {
                double[] m = kernel.get(i, j);
                for (int k = 0; k < m.length; k++) {
                    m[k] = m[k] / (KERNEL_SIZE * KERNEL_SIZE);
                }
                kernel.put(i, j, m);
            }
        }
        Imgproc.filter2D(source, source, -1, kernel);
        return source;
    }
}
