package lua

import org.luaj.vm2.LuaNumber
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import util.setFunction
import util.setProcedure
import java.awt.GraphicsDevice
import java.awt.Robot
import java.awt.image.BufferedImage

class LibScreen(device: GraphicsDevice) : LuaTable() {

    val robot: Robot = Robot(device)
    val device: GraphicsDevice
    var screen: BufferedImage

    init {
        screen = robot.createScreenCapture(device.defaultConfiguration.bounds)
        this.device = device

        setProcedure("poll", {
            screen = robot.createScreenCapture(device.defaultConfiguration.bounds)
        })

        setFunction("pixelAt", {
            val rgb = screen.getRGB(it.checkint(1), it.checkint(2))

            return@setFunction LuaValue.varargsOf(arrayOf<LuaValue>(
                    LuaNumber.valueOf(rgb and 0xFF0000 shr 16),
                    LuaNumber.valueOf(rgb and 0x00FF00 shr 8),
                    LuaNumber.valueOf(rgb and 0x0000FF)
            ))
        })
    }
}