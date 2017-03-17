package lua

import org.jnativehook.mouse.NativeMouseEvent
import org.luaj.vm2.LuaNumber
import org.luaj.vm2.LuaTable

/**
 * Created by basti on 09.03.2017.
 */
object KeyTable : LuaTable() {
    init {
        Class.forName("org.jnativehook.keyboard.NativeKeyEvent").fields.forEach {
            if (it.name.startsWith("VC_")) {
                var name: String = it.name.replaceFirst("VC_", "")
                name = name.toLowerCase().replace(Regex("_(.)"), fun(result: MatchResult): CharSequence {
                    return result.groupValues[1].toUpperCase()
                })
                set(name, LuaNumber.valueOf(it.get(null) as Int))
            }
        }
    }
}

object ButtonTable : LuaTable() {
    init {
        set("left", NativeMouseEvent.BUTTON1)
        set("middle", NativeMouseEvent.BUTTON2)
        set("right", NativeMouseEvent.BUTTON3)
    }
}