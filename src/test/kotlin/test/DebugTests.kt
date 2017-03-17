package test

import org.jnativehook.keyboard.NativeKeyEvent
import org.junit.Test
import util.HookThread

/**
 * Created by basti on 09.03.2017.
 */
class DebugTests {

    @Test
    fun debug() {
        HookThread.start()
        Class.forName("org.jnativehook.keyboard.NativeKeyEvent").fields.forEach {
            if (it.name.startsWith("VC_")) {
                println("${it.name} -> ${NativeKeyEvent.getKeyText(it.get(null) as Int)}")
            }
        }
    }
}