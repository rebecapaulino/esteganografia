import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImageHelper {
    fun saveImage(image: BufferedImage, file: File): Boolean {
        return try {
            ImageIO.write(image, "png", file)
        }

        catch (e: Exception) {
            false
        }
    }

    fun getBufferedImage(file: File): BufferedImage? {
        return try {
            ImageIO.read(file)
        }

        catch (e: Exception) {
            null
        }
    }
}