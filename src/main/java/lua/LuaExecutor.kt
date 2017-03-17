package lua

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaNumber
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.JsePlatform
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.nio.file.Files
import java.nio.file.Path

/**
 * Created by yenon on 2/28/17.
 */
class LuaExecutor {
    var globals: Globals = JsePlatform.standardGlobals()
    val libIo: LibIO = LibIO()

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

    init {
        init()
    }

    fun init() {
        globals = JsePlatform.standardGlobals()
        libIo.clear()
        globals.set("sleep", object : VarArgFunction() {
            override fun invoke(v: Varargs): Varargs {
                Thread.sleep(v.checklong(1))
                return LuaValue.NIL
            }
        })
        globals.set("time", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                if (args.isnumber(1)) {
                    return LuaNumber.valueOf((System.currentTimeMillis() + args.checkint(1)).toInt())
                } else {
                    return LuaNumber.valueOf(System.currentTimeMillis().toInt())
                }
            }
        })
        globals.set("wrapScreen", object : VarArgFunction() {
            override fun invoke(v: Varargs): Varargs {
                return screenIdToDevice(v, object : DeviceToLua() {
                    override fun transform(device: GraphicsDevice): LuaValue {
                        return LibScreen(device)
                    }
                })
            }
        })
        globals.set("createOverlay", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                return LibOverlay(args.checkint(1),
                        args.checkint(2),
                        args.checkint(3),
                        args.checkint(4))
            }
        })

        globals.set("key", KeyTable)
        globals.set("button", ButtonTable)
        globals.set("io", libIo)
    }

    fun exec(script: String): LuaValue {
        return globals.load(script).call()
    }

    fun exec(path: Path): LuaValue {
        return globals.load(Files.newInputStream(path).reader(), path.fileName.toString()).call()
    }

    fun clear() {
        init()
    }

    //fun serializeTable(value:LuaValue):String{

    //}
}
