package lua

import lua.swing.LibOverlay
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaNumber
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.jse.JsePlatform
import util.setFunction
import util.setProcedure
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.util.concurrent.FutureTask

class LuaExecutor {
    var globals: Globals = JsePlatform.standardGlobals()
    val libIo: LibIO = LibIO()
    val overlays = ArrayList<LibOverlay>()

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
        globals.setProcedure("sleep", {
            Thread.sleep(it.checklong(1))
        })
        globals.setFunction("time", {
            return@setFunction LuaNumber.valueOf((System.currentTimeMillis() + it.optint(1, 0)).toInt())
        })
        globals.setFunction("wrapScreen", {
            return@setFunction screenIdToDevice(it, object : DeviceToLua() {
                override fun transform(device: GraphicsDevice): LuaValue {
                    return LibScreen(device)
                }
            })
        })
        globals.setFunction("createOverlay", {
            val overlay = LibOverlay(it.checkint(1),
                    it.checkint(2),
                    it.checkint(3),
                    it.checkint(4))
            overlays.add(overlay)
            return@setFunction overlay
        })

        globals.set("key", KeyTable)
        globals.set("button", ButtonTable)
        globals.set("io", libIo)
    }

    fun exec(script: String): LuaValue {
        val task = FutureTask<LuaValue>({
            return@FutureTask globals.load(script).call()
        })
        Thread(task).start()
        return task.get()
    }

    fun clear() {
        overlays.forEach({
            it.root = null
            it.view!!.hide()
            it.view = null
        })
        overlays.clear()
        init()
    }

    //fun serializeTable(value:LuaValue):String{

    //}
}
