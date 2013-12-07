package pl.project13.common.utils;

/**
 * File: Histogram.java
 * Date: December 30, 2012
 * Author: Ferad Zyulkyarov (feradz@gmail.com)
 *
 * This file implements a Histogram class which abstracts the histograms objects for
 * color and grayscale images. Instances of this class contain:
 * <ul>
 *   <li>
 *   	Histogram for a gray scale image which is obtained by converting a (color)
 *      image into a gray scale image using the available filters in Java framework and
 *      then reading the values representing the gray pixels.
 *   </li>
 *   <li>
 *      Histogram for the combined red(R), green(G) and blue (B) pixel values which is
 *      obtained based on the following formula:
 *      RGB_Combined = R*0.2126 + G*0.7152 + B*0.0722.
 *      This weighted formula represents the luminance intensity for a given pixel.
 *      This is the CIE 1931 standard way for computing luminance for sRGB images.
 *   </li>
 *   <li>
 *      Histogram for the combined red(R), green(G) and blue (B) pixel values which is
 *      obtained based on the following formula:
 *      RGB_Combined = R*0.299 + G*0.587 + B*0.114.
 *      This weighted formula represents the perceived luminance intensity for a given
 *      pixel.
 *   </li>
 *   <li>
 *     Histogram for the red channel.
 *   </li>
 *   <li>
 *     Histogram for the green channel.
 *   </li>
 *   <li>
 *     Histogram for the blue channel.
 *   </li>
 * </ul>
 *
 * @see <a href="http://blog.feradz.com/index.php/2013/01/06/image-histogram-java-tool/">http://blog.feradz.com/index.php/2013/01/06/image-histogram-java-tool/</a>
 * @license "Yes, go ahead use for whatever you want – commercial, home use."
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

/**
 * Abstracts an image histogram object.
 */
public class Histogram {

    /**
     * The gray histogram obtained using the method {@link #parseGrayHistogram(BufferedImage)}.
     * This histogram is calculated by converting the color image into a gray scale image
     * using the filters coming with the Java framework. We do not know how exactly
     * the conversion is done.
     */
    private int[] _grayHistogram;

    /**
     * The histogram for the red channel of a RGB color image.
     */
    private int[] _redHistogram;

    /**
     * The histogram for the green channel of a RGB color image.
     */
    private int[] _greenHistogram;

    /**
     * The histogram for the blue channel of a RGB color image.
     */
    private int[] _blueHistogram;

    /**
     * The histogram combined for the red, green and blue colors. This histogram
     * represents a histogram for a gray scale image which can be obtained from
     * the color image based on CIE 1931 standard. The following formula is used
     * to obtain this histogram: RGB_Combined = R*0.2126 + G*0.7152 + B*0.0722.
     */
    private int[] _computedGrayHistogram1;

    /**
     * The histogram combined for the red, green and blue colors. This histogram
     * represents a histogram for a gray scale image which can be obtained from
     * the color image. The following formula is used to obtain this histogram:
     * RGB_Combined = R*0.299 + G*0.587 + B*0.114.
     */
    private int[] _computedGrayHistogram2;

    /**
     * The luminance histogram.
     */
    private int[] _lumincanceHistogram;

    /**
     * The file name of the image.
     */
    private String _fileName;

    /**
     * A constructor for a Histogram object.
     *
     * @param fileName the name of the image file to compute the histogram.
     */
    private Histogram(String fileName) {
        _fileName = fileName;
        _grayHistogram = new int[256];
        _computedGrayHistogram1 = new int[256];
        _computedGrayHistogram2 = new int[256];
        _redHistogram = new int[256];
        _greenHistogram = new int[256];
        _blueHistogram = new int[256];
        _lumincanceHistogram = new int[256];
    }

    /**
     * A factory object which creates a histogram object for an image.
     *
     * @param fileName the file name of the image to compute a histogram.
     * @return A histogram object for the specified image.
     * @throws IOException if the specified image file does not exists or
     *                     there is some problem reading it.
     */
    public static Histogram getHisrogram(String fileName) throws IOException {
        File f = new File(fileName);
        BufferedImage img = ImageIO.read(f);
        Histogram h = new Histogram(fileName);
        h.parseGrayHistogram(img);
        h.parseHistogramForColors(img);
        return h;
    }

    /**
     * Compute the histogram of an image by converting it to a gray scale image
     * using the available filters in Java.
     *
     * @param img {@link BufferedImage}  which representation the image.
     */
    private void parseGrayHistogram(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        BufferedImage grayImg = convertToGrayscale(img);
        byte[] pix = new byte[1];
        int pixInt = 0;
        Raster grayImgRaster = grayImg.getRaster();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grayImgRaster.getDataElements(x, y, pix);
                pixInt = (int) (pix[0]) & 0xFF;
                _grayHistogram[pixInt]++;
            }
        }
    }

    /**
     * Compute the histograms for the red, green, blue and their combinations.
     *
     * @param img {@link BufferedImage} object which represents the image.
     */
    private void parseHistogramForColors(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        Color pixelColor = null;
        int computedGray = 0;
        boolean isGray = true;
        int r = 0;
        int g = 0;
        int b = 0;
        int luminance = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelColor = new Color(img.getRGB(x, y));
                r = pixelColor.getRed();
                g = pixelColor.getGreen();
                b = pixelColor.getBlue();

                _redHistogram[r]++;
                _greenHistogram[g]++;
                _blueHistogram[b]++;

                if (!(r == g && g == b)) {
                    isGray = false;
                }

                computedGray = computeGrayColor1(pixelColor);
                _computedGrayHistogram1[computedGray]++;
                computedGray = computeGrayColor2(pixelColor);
                _computedGrayHistogram2[computedGray]++;

                luminance = computeLuminance(pixelColor);
                _lumincanceHistogram[luminance]++;
            }
        }

        if (isGray) {
            //
            // If for all pixel in the image the red, green and blue values were equal,
            // this is a gray scale image. Then use directly one of the computed histograms.
            //
            _computedGrayHistogram1 = _redHistogram;
            _computedGrayHistogram2 = _redHistogram;
        }
    }

    /**
     * Converts a {@link BufferedImage} object into a gray scale image using
     * the available filters in Java.
     *
     * @param src The {@link BufferedImage} to be converted.
     * @return a {@link BufferedImage} which is converted in gray scale.
     */
    private static BufferedImage convertToGrayscale(BufferedImage src) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        BufferedImageOp op = new ColorConvertOp(cs, null);
        BufferedImage grayImg = op.filter(src, null);
        return grayImg;
    }

    /**
     * Computes the combined histogram for a color pixel based on the
     * following formula: RGB_Combined = R*0.2126 + G*0.7152 + B*0.0722
     *
     * @param c the color of a pixel.
     * @return the combined value which is computed from R, G and B colors.
     */
    private static int computeGrayColor1(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int gray = (int) ((double) (r * 0.2126) + (double) (g * 0.7152) + (double) (b * 0.0722));
        return gray;
    }

    /**
     * Computes the combined histogram for a color pixel based on the
     * following formula: RGB_Combined = R*0.299 + G*0.587 + B*0.114
     *
     * @param c the color of a pixel.
     * @return the combined value which is computed from R, G and B colors.
     */
    private static int computeGrayColor2(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int gray = (int) ((double) (r * 0.299) + (double) (g * 0.587) + (double) (b * 0.114));
        return gray;
    }

    /**
     * Computes the luminance of a color pixel.
     *
     * @param c the color values.
     * @return the corresponding lumincancevalue.
     */
    private static int computeLuminance(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int max = Math.max(Math.max(r, g), b);
        int min = Math.min(Math.min(r, g), b);
        int luminance = (max + min) / 2;
        return luminance;
    }

    /**
     * @return The gray histogram calculated by converting the color image into a
     * gray scale image using the filters coming with the Java framework. We do
     * not know how exactly the conversion is done.
     */
    public int[] getGrayHistogram() {
        return _grayHistogram;
    }

    /**
     * @return The histogram combined for the red, green and blue colors. This histogram
     * represents a histogram for a gray scale image which can be obtained from
     * the color image based on CIE 1931 standard. The following formula is used
     * to obtain this histogram: RGB_Combined = R*0.2126 + G*0.7152 + B*0.0722.
     */
    public int[] getComputedGrayHistogram1() {
        return _computedGrayHistogram1;
    }

    /**
     * @return The histogram combined for the red, green and blue colors. This histogram
     * represents a histogram for a gray scale image which can be obtained from
     * the color image. The following formula is used to obtain this histogram:
     * RGB_Combined = R*0.299 + G*0.587 + B*0.114.
     */
    public int[] getComputedGrayHistogram2() {
        return _computedGrayHistogram2;
    }

    /**
     * @return The histogram for the red channel.
     */
    public int[] getRedHistogram() {
        return _redHistogram;
    }

    /**
     * @return The histogram for the green channel.
     */
    public int[] getGreenHistogram() {
        return _greenHistogram;
    }

    /**
     * @return The histogram for the blue channel.
     */
    public int[] getBlueHistogram() {
        return _blueHistogram;
    }

    /**
     * @return The luminance histogram.
     */
    public int[] getLuminanceHistogram() {
        return _lumincanceHistogram;
    }

    /**
     * @return The file name of the image.
     */
    public String getFileName() {
        return _fileName;
    }

    /**
     * Normalizes the values of a histogram.
     *
     * @param height    the maximum value to normalize the values.
     * @param histogram the histogram to be normalized.
     * @return a histogram normalized based on @height.
     */
    public static int[] normalize(int height, int[] histogram) {
        int max = 0;
        int ratio = 0;
        int[] res = new int[histogram.length];
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > max) {
                max = histogram[i];
            }
        }

        ratio = max / height;
        for (int i = 0; i < res.length; i++) {
            res[i] = histogram[i] / ratio;
        }

        return res;
    }

    /**
     * Smooths a histogram.
     *
     * @param h a histogram to be smoothed.
     * @return a smoothed histogram.
     */
    public static int[] smooth(int[] original) {
        int[] smoothed = new int[original.length];
        double[] mask = new double[]{0.25, 0.5, 0.25};

        smoothed[0] = original[0];
        smoothed[original.length - 1] = original[original.length - 1];

        for (int histInd = 1; histInd < original.length - 1; histInd++) {
            double smoothedValue = 0;
            for (int maskInd = 0; maskInd < mask.length; maskInd++) {
                smoothedValue += original[histInd - 1 + maskInd] * mask[maskInd];
            }
            smoothed[histInd] = (int) smoothedValue;
        }

        return smoothed;
    }
}
