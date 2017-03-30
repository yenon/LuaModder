import lua.Plugin
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment

class TesseractPlugin : Plugin {

    abstract class DeviceToLua {
        abstract fun transform(device: GraphicsDevice): LuaValue
    }

    fun screenIdToDevice(v: Varargs, transform: DeviceToLua): LuaValue {
        val screen: Int = v.checkint(1)
        if (screen >= 0 && screen < GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.size) {
            return transform.transform(GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices[screen])
        } else {
            LuaValue.error("Screen $screen does not exist.")
            return LuaValue.NIL
        }
    }

    override fun onLoad(globals: Globals) {
        globals.set("createTesseract", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                return screenIdToDevice(args, object : DeviceToLua() {
                    override fun transform(device: GraphicsDevice): LuaValue {
                        return TesseractLib(device)
                    }
                })
            }
        })
    }

    override fun clear() {

    }
}