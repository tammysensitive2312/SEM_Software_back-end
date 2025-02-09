package org.example.sem_backend.modules.document_module.core.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractFileParser implements DocumentParser {
    protected String filePath;

    private static final int HASH_SIZE = 32; // Kích thước ảnh để tính pHash
    protected static final int SIMILARITY_THRESHOLD = 10; // Ngưỡng để xác định ảnh tương tự (càng nhỏ càng nghiêm ngặt)

    /**
     * @param filePath
     */
    @Override
    public void parseDocument(String filePath) {
    }

    private static void extractText(File file, String outputDir) throws IOException {
    }

    private static void extractImages(File file, String outputDir) throws IOException {
    }

    protected static String calculatePHash(BufferedImage image) {
        // Bước 1: Resize về kích thước cố định
        BufferedImage resizedImage = resizeImage(image, HASH_SIZE, HASH_SIZE);

        // Bước 2: Chuyển sang ảnh xám
        double[][] grayImage = new double[HASH_SIZE][HASH_SIZE];
        for (int i = 0; i < HASH_SIZE; i++) {
            for (int j = 0; j < HASH_SIZE; j++) {
                Color color = new Color(resizedImage.getRGB(i, j));
                grayImage[i][j] = 0.299 * color.getRed() +
                        0.587 * color.getGreen() +
                        0.114 * color.getBlue();
            }
        }

        // Bước 3: Tính giá trị trung bình
        final var hash = getHash(grayImage);

        return hash.toString();
    }

    private static StringBuilder getHash(double[][] grayImage) {
        double total = 0;
        for (int i = 0; i < HASH_SIZE; i++) {
            for (int j = 0; j < HASH_SIZE; j++) {
                total += grayImage[i][j];
            }
        }
        double average = total / (HASH_SIZE * HASH_SIZE);

        // Bước 4: Tạo hash string dựa trên so sánh với giá trị trung bình
        StringBuilder hash = new StringBuilder();
        for (int i = 0; i < HASH_SIZE; i++) {
            for (int j = 0; j < HASH_SIZE; j++) {
                hash.append(grayImage[i][j] > average ? "1" : "0");
            }
        }
        return hash;
    }

    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }

    protected static int hammingDistance(String hash1, String hash2) {
        if (hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("Hash lengths must be equal");
        }

        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }
}
