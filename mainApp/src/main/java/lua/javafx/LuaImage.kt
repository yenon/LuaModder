package lua.javafx

import Main
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import util.setFXProcedure
import java.nio.file.Files

class LuaImage(x: Int, y: Int) : LuaNode<ImageView>() {
    override val e: ImageView = ImageView()
    var image: WritableImage? = null

    init {
        e.x = x.toDouble()
        e.y = y.toDouble()
        e.isMouseTransparent = true
        e.isFocusTraversable = false

        setFXProcedure("setImage", {
            val newImage = Image(Files.newInputStream(Main.path.resolve("scripts/${it.checkjstring(1)}")))
            image = WritableImage(newImage.pixelReader, newImage.width.toInt(), newImage.height.toInt())
            e.image = image
        })

        setFXProcedure("setTint", {
            val img = image ?: return@setFXProcedure
            val imgReader = img.pixelReader
            val color = (it.checkint(1) shl 16) or (it.checkint(2) shl 8) or it.checkint(3)
            val tintedImage = WritableImage(img.width.toInt(), img.height.toInt())
            val imgWriter = tintedImage.pixelWriter
            for (pxY in 0..img.height.toInt() - 1) {
                for (pxX in 0..img.width.toInt() - 1) {
                    val value = imgReader.getArgb(pxX, pxY) shr 24
                    imgWriter.setArgb(pxX, pxY,
                            color or (value shl 24))
                }
            }
            e.image = tintedImage
        })
    }
}