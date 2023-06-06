package io;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ImageHandler {

    private final String IMG_FORMAT = "png";

    // Error Messages
    private final String ERROR_ENCODE = "Error encoding image.";
    private final String ERROR_DECODE = "Error decoding image byte string.";

    /**
     * ImageHandler default constructor
     */
    public ImageHandler() {}

    /**
     * Encodes the image in Base64 format and into a string
     * @param image canvas
     * @return encoded image string
     * @throws ImageHandlerException error encoding the image
     */
    public String getImageString(BufferedImage image) throws ImageHandlerException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] imageBytes;
        try {
            ImageIO.write(image, IMG_FORMAT, b);
            imageBytes = b.toByteArray();
            String imageString = Base64.getEncoder().encodeToString(imageBytes);
            b.close();
            return imageString;
        } catch (Exception e) {
           throw new ImageHandlerException(ERROR_ENCODE);
        }
    }

    /**
     * Decodes the Base64 encoded image string
     * @param imageString encoded image string
     * @return image
     * @throws ImageHandlerException error decoding the image string
     */
    public BufferedImage convertImageString(String imageString) throws ImageHandlerException {
        try {
            return ImageIO.read(
                    new ByteArrayInputStream(Base64.getDecoder().decode(imageString)));
        } catch (Exception e) {
            throw new ImageHandlerException(ERROR_DECODE);
        }
    }
}