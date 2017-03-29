package util

import javafx.beans.property.SimpleObjectProperty
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseListener
import org.jnativehook.mouse.NativeMouseMotionListener
import java.util.logging.Level
import java.util.logging.Logger

object HookThread : Thread(), NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {

    val mouseUpEventProperty: SimpleObjectProperty<NativeMouseEvent> = SimpleObjectProperty()
    val mouseDownEventProperty: SimpleObjectProperty<NativeMouseEvent> = SimpleObjectProperty()
    val keyUpEventProperty: SimpleObjectProperty<NativeKeyEvent> = SimpleObjectProperty()
    val keyDownEventProperty: SimpleObjectProperty<NativeKeyEvent> = SimpleObjectProperty()

    override fun run() {
        println("Starting JNativehook")

        val logger = Logger.getLogger(GlobalScreen::class.java.`package`.name)
        logger.level = Level.OFF

        // Change the level for all handlers attached to the default logger.
        val handlers = Logger.getLogger("").handlers
        for (handler in handlers) {
            handler.level = Level.OFF
        }

        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(this)
            GlobalScreen.addNativeMouseListener(this)
            GlobalScreen.addNativeMouseMotionListener(this)
            println("Init finished")
            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    println("Stopping JNativehook")
                    GlobalScreen.unregisterNativeHook()
                }
            })
        } catch (e: NativeHookException) {
            println("Error starting JNativehook!")
            e.printStackTrace()
            println("---------------------------")
        }
    }

    val debug: Boolean = false

    fun printKeyEvent(prefix: String, event: NativeKeyEvent) {
        if (debug) {
            println("$prefix\n" +
                    "id       : ${event.id}\n" +
                    "modifiers: ${event.modifiers}\n" +
                    "rawCode  : ${event.rawCode}\n" +
                    "keyCode  : ${event.keyCode}\n" +
                    "keyChar  : ${event.keyChar.toInt()}\n" +
                    "location : ${event.keyLocation}\n" +
                    "-------------------------")
        }
    }

    fun printMouseEvent(prefix: String, event: NativeMouseEvent) {
        if (debug) {
            println("$prefix\n" +
                    "id       : ${event.id}\n" +
                    "modifiers: ${event.modifiers}\n" +
                    "x        : ${event.x}\n" +
                    "y        : ${event.y}\n" +
                    "clickCnt : ${event.clickCount}\n" +
                    "button   : ${event.button}\n" +
                    "-------------------------")
        }
    }

    override fun nativeKeyTyped(nativeKeyEvent: NativeKeyEvent) {
        printKeyEvent("Typed", nativeKeyEvent)
    }

    override fun nativeKeyPressed(nativeKeyEvent: NativeKeyEvent) {
        keyDownEventProperty.value = nativeKeyEvent
        printKeyEvent("Down", nativeKeyEvent)
    }

    override fun nativeKeyReleased(nativeKeyEvent: NativeKeyEvent) {
        keyUpEventProperty.value = nativeKeyEvent
        printKeyEvent("Up", nativeKeyEvent)
    }

    override fun nativeMouseClicked(nativeMouseEvent: NativeMouseEvent) {
        printMouseEvent("Clicked", nativeMouseEvent)
    }

    override fun nativeMousePressed(nativeMouseEvent: NativeMouseEvent) {
        mouseDownEventProperty.value = nativeMouseEvent
        printMouseEvent("Down", nativeMouseEvent)
    }

    override fun nativeMouseReleased(nativeMouseEvent: NativeMouseEvent) {
        mouseUpEventProperty.value = nativeMouseEvent
        printMouseEvent("Up", nativeMouseEvent)
    }

    override fun nativeMouseMoved(p0: NativeMouseEvent) {
        printMouseEvent("Moved", p0)
    }

    override fun nativeMouseDragged(p0: NativeMouseEvent) {
        printMouseEvent("Dragged", p0)
    }
}
