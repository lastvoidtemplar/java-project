package image.processing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ImageColorQuantizer {
    private long pixelDist(Pixel a, Pixel b) {
        long redDiff = a.red() - b.red();
        long greenDiff = a.green() - b.green();
        long blueDiff = a.blue() - b.blue();
        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }

    private int weightedRandom(Random rnd, List<Long> weights) {
        List<Long> prefix = new ArrayList<>();
        long sum = 0;
        for (Long weight : weights) {
            sum = Math.addExact(sum, weight);
            prefix.add(sum);
        }
        long num = rnd.nextLong(0, sum);
        int ind = Collections.binarySearch(prefix, num);
        return ind >= 0 ? ind : -ind - 1;
    }

    private List<Pixel> generateRandomCentroids(List<Pixel> pixels, int k) {
        Random rnd = new Random();
        List<Pixel> centroids = new ArrayList<>();
        List<Long> dists = new ArrayList<>();
        centroids.add(pixels.get(rnd.nextInt(0, pixels.size())));
        for (int centroidInd = 1; centroidInd < k; centroidInd++) {
            for (Pixel pixel : pixels) {
                long minDist = Long.MAX_VALUE;
                for (Pixel centroid : centroids) {
                    minDist = Long.min(minDist, pixelDist(pixel, centroid));
                }
                dists.add(minDist);
            }
            centroids.add(pixels.get(weightedRandom(rnd, dists)));
            dists.clear();
        }
        return centroids;
    }

    private static final int COLOR_COUNT = 3;
    private static final int RED_INDEX = 0;
    private static final int GREEN_INDEX = 1;
    private static final int BLUE_INDEX = 2;

    private List<Pixel> clusterPixels(List<Pixel> pixels, List<Pixel> initialCentroids) {
        List<Pixel> centroids = initialCentroids;
        boolean sameCentroids = false;
        while (!sameCentroids) {
            List<long[]> sums = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            int centroidsSize = centroids.size();
            fillSumAndCountLists(sums, counts, centroidsSize);
            for (Pixel pixel : pixels) {
                int centroidInd = findClosestCentroidIndex(centroids, pixel);
                updateSumAndCountLists(sums, counts, centroidInd, pixel);
            }

            List<Pixel> newCentroids = computeNewCentroids(sums, counts, centroids);
            sameCentroids = centroids.equals(newCentroids);
            centroids = newCentroids;
        }
        return centroids;
    }

    private void fillSumAndCountLists(List<long[]> sums, List<Integer> counts, int centroidsSize) {
        for (int i = 0; i < centroidsSize; i++) {
            sums.add(new long[COLOR_COUNT]);
            counts.add(0);
        }
    }

    private void updateSumAndCountLists(List<long[]> sums, List<Integer> counts, int centroidInd, Pixel pixel) {
        sums.get(centroidInd)[RED_INDEX] += pixel.red();
        sums.get(centroidInd)[GREEN_INDEX] += pixel.green();
        sums.get(centroidInd)[BLUE_INDEX] += pixel.blue();
        counts.set(centroidInd, counts.get(centroidInd) + 1);
    }

    private int findClosestCentroidIndex(List<Pixel> centroids, Pixel pixel) {
        int centroidsSize = centroids.size();
        long minDist = Long.MAX_VALUE;
        int minInd = -1;
        for (int centroidInd = 0; centroidInd < centroidsSize; centroidInd++) {
            Pixel centroid = centroids.get(centroidInd);
            long dist = pixelDist(pixel, centroid);
            if (dist < minDist) {
                minInd = centroidInd;
                minDist = dist;
            }
        }
        return minInd;
    }

    private List<Pixel> computeNewCentroids(List<long[]> sums, List<Integer> counts, List<Pixel> centroids) {
        int centroidsSize = centroids.size();
        List<Pixel> newCentroids = new ArrayList<>();
        for (int centroidInd = 0; centroidInd < centroidsSize; centroidInd++) {
            long[] sum = sums.get(centroidInd);
            int count = counts.get(centroidInd);
            if (count == 0) {
                newCentroids.add(centroids.get(centroidInd));
                continue;
            }
            Pixel newCentroid = new Pixel(
                sum[RED_INDEX] / count,
                sum[GREEN_INDEX] / count,
                sum[BLUE_INDEX] / count
            );
            newCentroids.add(newCentroid);
        }
        return newCentroids;
    }

    private static final int RED_OFFSET = 16;
    private static final int GREEN_OFFSET = 8;
    private static final int BLUE_OFFSET = 0;
    private static final int RED_MASK = 0b1111_1111 << RED_OFFSET;
    private static final int GREEN_MASK = 0b1111_1111 << GREEN_OFFSET;
    private static final int BLUE_MASK = 0b1111_1111 << BLUE_OFFSET;

    private List<Pixel> convertImageToPixelList(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        List<Pixel> pixels = new ArrayList<>(height * width);
        for (int row = 0; row < height; row++) {
            for (int colm = 0; colm < width; colm++) {
                int color = image.getRGB(colm, row);
                short red = (short) ((color & RED_MASK) >> RED_OFFSET);
                short green = (short) ((color & GREEN_MASK) >> GREEN_OFFSET);
                short blue = (short) ((color & BLUE_MASK) >> BLUE_OFFSET);
                pixels.add(new Pixel(red, green, blue));
            }
        }
        return pixels;
    }

    private BufferedImage convertPixelsToImage(List<Pixel> pixels, List<Pixel> centroids, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            for (int colm = 0; colm < width; colm++) {
                Pixel pixel = pixels.get(row * width + colm);
                long minDist = Long.MAX_VALUE;
                Pixel closestCentroid = centroids.getFirst();
                for (Pixel centroid : centroids) {
                    long dist = pixelDist(pixel, centroid);
                    if (dist < minDist) {
                        closestCentroid = centroid;
                        minDist = dist;
                    }
                }
                int color = (int) (closestCentroid.red() << RED_OFFSET | closestCentroid.green() << GREEN_OFFSET |
                    closestCentroid.blue() << BLUE_OFFSET);
                image.setRGB(colm, row, color);
            }
        }
        return image;
    }

    public BufferedImage colorQuantize(BufferedImage source, int k) {
        List<Pixel> pixels = convertImageToPixelList(source);
        List<Pixel> initialCentroids = generateRandomCentroids(pixels, k);
        List<Pixel> centroids = clusterPixels(pixels, initialCentroids);
        return convertPixelsToImage(pixels, centroids, source.getWidth(), source.getHeight());
    }
}
