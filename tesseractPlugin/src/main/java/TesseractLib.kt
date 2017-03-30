import net.sourceforge.tess4j.Tesseract
import org.luaj.vm2.LuaString
import org.luaj.vm2.LuaTable
import org.luaj.vm2.Varargs
import util.scale
import util.setFunction
import util.setProcedure
import java.awt.GraphicsDevice
import java.awt.Robot
import java.awt.image.BufferedImage

class TesseractLib(device: GraphicsDevice) : LuaTable() {
    val robot: Robot = Robot(device)
    val device: GraphicsDevice
    var screen: BufferedImage
    val tesseract: Tesseract = Tesseract()

    private fun readString(v: Varargs): String {
        val x = v.checkint(1)
        val y = v.checkint(2)
        val width = v.checkint(3)
        val height = v.checkint(4)
        return tesseract.doOCR(screen.getSubimage(x, y, width, height)
                .scale(if (v.isnumber(5)) {
                    v.checkdouble(5)
                } else {
                    3.0
                }))
    }

    init {
        screen = robot.createScreenCapture(device.defaultConfiguration.bounds)
        this.device = device

        setProcedure("poll", {
            screen = robot.createScreenCapture(device.defaultConfiguration.bounds)
        })

        setFunction("readString", {
            tesseract.setTessVariable("tessedit_char_whitelist", "")
            tesseract.setTessVariable("tessedit_char_blacklist", "\n")
            return@setFunction LuaString.valueOf(readString(it))
        })

        setFunction("readNumber", {
            tesseract.setTessVariable("tessedit_char_whitelist", "0123456789.,")
            return@setFunction LuaString.valueOf(readString(it).replace(",", "."))
        })
    }
}
