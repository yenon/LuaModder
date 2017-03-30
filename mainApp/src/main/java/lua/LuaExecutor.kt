package lua

import Main
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.FutureTask
import kotlin.collections.ArrayList

class LuaExecutor {
    var globals: Globals = JsePlatform.standardGlobals()
    val plugins: ArrayList<Plugin> = ArrayList()

    init {
        @Suppress("UNCHECKED_CAST")
        val urls: ArrayList<URL> = ArrayList()
        Main.path.resolve("plugins").toFile().listFiles().forEach {
            println("loaded!")
            urls.add(it.toURI().toURL())
        }

        val loader: ServiceLoader<Plugin> = ServiceLoader.load(
                Plugin::class.java,
                URLClassLoader(urls.toTypedArray()))

        loader.forEach {
            plugins.add(it)
            println("Plugin Found!")
        }

        init()
    }

    fun init() {
        globals = JsePlatform.standardGlobals()

        plugins.forEach {
            it.onLoad(globals)
        }
    }

    fun exec(script: String): LuaValue {
        val task = FutureTask<LuaValue>({
            return@FutureTask globals.load(script).call()
        })
        Thread(task).start()
        return task.get()
    }

    fun clear() {
        plugins.forEach(Plugin::clear)
        init()
    }

    //fun serializeTable(value:LuaValue):String{

    //}
}
