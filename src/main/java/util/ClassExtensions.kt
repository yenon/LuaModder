package util

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

/**
 * Created by basti on 11.03.2017.
 */

fun BufferedImage.scale(factor: Double): BufferedImage {
    val created: BufferedImage = BufferedImage((this.width * factor).toInt(), (this.height * factor).toInt(), this.type)
    created.createGraphics().drawRenderedImage(this, AffineTransform.getScaleInstance(factor, factor))
    return created
}