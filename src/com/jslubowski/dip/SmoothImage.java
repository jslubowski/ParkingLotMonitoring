package com.jslubowski.dip;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class SmoothImage {
    /*
    Class not used but attached for future use.
     */

    // == fields ==
    private static final int kernelSize = 3;

    // == methods ==
    public static Mat smoothGrayscaleImage(Mat source){
        Mat kernel = Mat.ones(kernelSize, kernelSize, CvType.CV_32F);
        for(int i = 0; i < kernel.rows(); i++) {
            for (int j = 0; j < kernel.cols(); j++) {
                double[] m = kernel.get(i, j);
                for (int k = 0; k < m.length; k++) {
                    m[k] = m[k] / (kernelSize * kernelSize);
                }
                kernel.put(i, j, m);
            }
        }
        Imgproc.filter2D(source, source, -1, kernel);
        return source;
    }
}
