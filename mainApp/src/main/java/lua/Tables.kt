package lua

import org.jnativehook.mouse.NativeMouseEvent
import org.luaj.vm2.LuaNumber
import org.luaj.vm2.LuaTable

object ReverseKeyLookup : HashMap<Int, String>()

object ReverseModifierLookup : HashMap<Int, String>() {
    val resolver: Array<String> = arrayOf(
            "shiftL",
            "ctrlL",
            "metaL",
            "altL",
            "shiftR",
            "ctrlR",
            "metaR",
            "altR",
            "button1",
            "button2",
            "button3",
            "button4",
            "button5",
            "numLock",
            "capsLock",
            "scrollLock")

    fun resolve(input: Int): String {
        val builder: StringBuilder = StringBuilder()
        var inp = input
        var i = 0
        while (inp != 0) {
            if (inp % 2 == 1) {
                builder.append(resolver[i]).append(",")
            }
            i++
            inp /= 2
        }
        if (builder.isNotBlank()) {
            builder.deleteCharAt(builder.length - 1)
        }
        return builder.toString()
    }
}

object KeyTable : LuaTable() {
    init {
        Class.forName("org.jnativehook.keyboard.NativeKeyEvent").fields.forEach {
            if (it.name.startsWith("VC_")) {
                var name: String = it.name.replaceFirst("VC_", "")
                name = name.toLowerCase().replace(Regex("_(.)"), fun(result: MatchResult): CharSequence {
                    return result.groupValues[1].toUpperCase()
                })
                if (name.matches(Regex("[0123456789]"))) {
                    name = "N" + name
                }
                set(name, LuaNumber.valueOf(it.get(null) as Int))
                ReverseKeyLookup.put(it.getInt(null), name)
            }
        }
    }
}

object ButtonTable : LuaTable() {
    init {
        set("left", NativeMouseEvent.BUTTON1)
        set("middle", NativeMouseEvent.BUTTON3)
        set("right", NativeMouseEvent.BUTTON2)
    }
}