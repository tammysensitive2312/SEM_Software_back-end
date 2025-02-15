package org.example.sem_backend.modules.document_module.core.service.parser_layer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class PdfFileParser extends AbstractFileParser {
    @Value("${upload.default.directory}")
    private String defaultDirectory;

    public PdfFileParser(String filePath) {
        super(filePath);
        parseDocument(filePath);
    }

    @Override
    public void parseDocument(String path) {
        File source = new File(path);
        if (source.isFile() && source.getName().endsWith(".pdf")) {
            try {
                // Tạo tên thư mục từ tên file PDF
                String fileName = source.getName().replaceFirst("[.][^.]+$", "");
                String outputDir = source.getParent() + File.separator + fileName;

                // Tạo thư mục nếu chưa tồn tại
                Files.createDirectories(Paths.get(outputDir));

                // Trích xuất text và hình ảnh
                extractText(source, outputDir);
                extractImages(source, outputDir);

            } catch (Exception e) {
                log.debug("Error parsing file {}: {}", source.getName(), e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Path must be a PDF file or directory containing PDF files");
        }
    }

    private static void extractText(File pdfFile, String outputDir) throws IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException {
        PDFParser pdfParser = new PDFParser();
        BodyContentHandler handler = new BodyContentHandler(-1); // -1 để không giới hạn độ dài text
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();


        try (FileInputStream inputStream = new FileInputStream(pdfFile)) {
            pdfParser.parse(inputStream, handler, metadata, context);

            // Lưu text vào file
            String textFile = outputDir + File.separator + "extracted_text.txt";
            try (FileWriter writer = new FileWriter(textFile)) {
                writer.write(handler.toString());
            }

            // Lưu metadata
            String metadataFile = outputDir + File.separator + "metadata.txt";
            try (FileWriter writer = new FileWriter(metadataFile)) {
                String[] metadataNames = metadata.names();
                for (String name : metadataNames) {
                    writer.write(name + ": " + metadata.get(name) + "\n");
                }
            }
        }
    }


    private static void extractImages(File pdfFile, String outputDir) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int imageCounter = 0;
            Map<String, String> savedImages = new HashMap<>(); // Map lưu hash và tên file

            for (PDPage page : document.getPages()) {
                PDResources resources = page.getResources();
                if (resources != null) {
                    for (COSName name : resources.getXObjectNames()) {
                        PDXObject xObject = resources.getXObject(name);

                        if (xObject instanceof PDImageXObject image) {
                            BufferedImage bufferedImage = image.getImage();

                            // Tính pHash cho ảnh hiện tại
                            String imageHash = calculatePHash(bufferedImage);

                            // Kiểm tra xem có ảnh tương tự không
                            boolean isDuplicate = false;
                            String similarImageName = null;

                            for (Map.Entry<String, String> entry : savedImages.entrySet()) {
                                int distance = hammingDistance(imageHash, entry.getKey());
                                if (distance < SIMILARITY_THRESHOLD) {
                                    isDuplicate = true;
                                    similarImageName = entry.getValue();
                                    break;
                                }
                            }

                            if (!isDuplicate) {
                                String imageFormat = "png";
                                String imageFileName = String.format("image_%04d.%s", imageCounter++, imageFormat);
                                String imagePath = outputDir + File.separator + imageFileName;

                                ImageIO.write(bufferedImage, imageFormat, new File(imagePath));
                                savedImages.put(imageHash, imageFileName);

                                System.out.printf("Extracted unique image: %s (Size: %dx%d)%n",
                                        imageFileName, image.getWidth(), image.getHeight());
                            } else {
                                System.out.printf("Skipped similar image to %s (Size: %dx%d)%n",
                                        similarImageName, image.getWidth(), image.getHeight());
                            }
                        }
                    }
                }
            }
            System.out.println("Total unique images extracted: " + imageCounter);
        }
    }


}
