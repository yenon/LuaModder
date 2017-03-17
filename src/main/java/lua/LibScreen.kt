package lua

import net.sourceforge.tess4j.Tesseract
import org.luaj.vm2.*
import org.luaj.vm2.lib.VarArgFunction
import util.scale
import java.awt.GraphicsDevice
import java.awt.Robot
import java.awt.image.BufferedImage

class LibScreen(device: GraphicsDevice) : LuaTable() {

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
        tesseract.setTessVariable("tessedit_char_blacklist", "\n")
        screen = robot.createScreenCapture(device.defaultConfiguration.bounds)
        this.device = device

        set("poll", object : VarArgFunction() {
            override fun invoke(v: Varargs): Varargs {
                screen = robot.createScreenCapture(device.defaultConfiguration.bounds)
                return LuaValue.NIL
            }
        })

        set("pixelAt", object : VarArgFunction() {
            override fun invoke(v: Varargs): Varargs {
                val rgb = screen.getRGB(v.checkint(1), v.checkint(2))

                return LuaValue.varargsOf(arrayOf<LuaValue>(
                        LuaNumber.valueOf(rgb and 0xFF0000 shr 16),
                        LuaNumber.valueOf(rgb and 0x00FF00 shr 8),
                        LuaNumber.valueOf(rgb and 0x0000FF)
                ))
            }
        })

        set("readString", object : VarArgFunction() {
            override fun invoke(v: Varargs): Varargs {
                tesseract.setTessVariable("tessedit_char_whitelist", "")
                tesseract.setTessVariable("tessedit_char_blacklist", "\n")
                return LuaString.valueOf(readString(v))
            }
        })

        set("readNumber", object : VarArgFunction() {
            override fun invoke(v: Varargs): Varargs {
                tesseract.setTessVariable("tessedit_char_whitelist", "0123456789.,")
                return LuaString.valueOf(readString(v).replace(',', '.'))
            }
        })
    }
}