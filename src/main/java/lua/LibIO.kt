package lua

import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseWheelEvent
import org.luaj.vm2.*
import util.HookThread
import util.setFunction
import util.setProcedure
import java.awt.MouseInfo
import java.awt.Point

class LibIO : LuaTable() {

    val mouseDownMap: HashMap<Int, LuaFunction> = HashMap()
    val mouseUpMap: HashMap<Int, LuaFunction> = HashMap()
    val keyDownMap: HashMap<Int, LuaFunction> = HashMap()
    val keyUpMap: HashMap<Int, LuaFunction> = HashMap()

    init {
        setProcedure("pressKey", {
            GlobalScreen.postNativeEvent(NativeKeyEvent(
                    NativeKeyEvent.NATIVE_KEY_PRESSED,
                    0,
                    0,
                    it.checkint(1),
                    NativeKeyEvent.CHAR_UNDEFINED))
        })
        setProcedure("releaseKey", {
            GlobalScreen.postNativeEvent(NativeKeyEvent(
                    NativeKeyEvent.NATIVE_KEY_RELEASED,
                    0,
                    0,
                    it.checkint(1),
                    NativeKeyEvent.CHAR_UNDEFINED))
        })

        setProcedure("pressMouse", {
            val location: Point = MouseInfo.getPointerInfo().location
            GlobalScreen.postNativeEvent(NativeMouseEvent(
                    NativeMouseEvent.NATIVE_MOUSE_PRESSED,
                    0,
                    optint(2, location.x),
                    optint(3, location.y),
                    0,
                    it.checkint(1)
            ))
        })
        setProcedure("releaseMouse", {
            val location: Point = MouseInfo.getPointerInfo().location

            GlobalScreen.postNativeEvent(NativeMouseEvent(
                    NativeMouseEvent.NATIVE_MOUSE_RELEASED,
                    0,
                    optint(2, location.x),
                    optint(3, location.y),
                    0,
                    it.checkint(1)
            ))
        })
        setProcedure("moveAbsolute", {
            GlobalScreen.postNativeEvent(NativeMouseEvent(
                    NativeMouseEvent.NATIVE_MOUSE_MOVED,
                    0, //Modifiers
                    it.checkint(1),
                    it.checkint(2),
                    0, //Clickcount
                    NativeMouseEvent.NOBUTTON
            ))
        })
        setProcedure("moveRelative", {
            val location: Point = MouseInfo.getPointerInfo().location
            GlobalScreen.postNativeEvent(NativeMouseEvent(
                    NativeMouseEvent.NATIVE_MOUSE_MOVED,
                    0,
                    location.x + it.checkint(1),
                    location.y + it.checkint(2),
                    0,
                    NativeMouseEvent.NOBUTTON
            ))
        })
        setProcedure("scroll", {
            GlobalScreen.postNativeEvent(NativeMouseWheelEvent(
                    NativeMouseWheelEvent.NATIVE_MOUSE_WHEEL,
                    0, //Modifiers
                    0, //x
                    0, //y
                    0, //Click count
                    NativeMouseWheelEvent.WHEEL_BLOCK_SCROLL,
                    0, //Unit rotation
                    it.checkint(1), //"Click" rotation
                    if (it.optboolean(2, true)) {
                        NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION
                    } else {
                        NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION
                    }
            ))
        })
        setFunction("getMousePos", {
            val location: Point = MouseInfo.getPointerInfo().location
            return@setFunction LuaValue.varargsOf(LuaNumber.valueOf(location.x), LuaNumber.valueOf(location.y))
        })

        HookThread.mouseDownEventProperty.addListener({ _, _, newValue ->
            mouseDownMap[newValue.button]?.call()
        })
        setProcedure("setMouseDownListener", { changeMap(mouseDownMap, it) })
        HookThread.mouseUpEventProperty.addListener({ _, _, newValue ->
            mouseUpMap[newValue.button]?.call()
        })
        setProcedure("setMouseUpListener", { changeMap(mouseUpMap, it) })
        HookThread.keyDownEventProperty.addListener({ _, _, newValue ->
            keyDownMap[newValue.keyCode]?.call()
        })
        setProcedure("setKeyDownListener", {
            changeMap(keyDownMap, it)
        })
        HookThread.keyUpEventProperty.addListener({ _, _, newValue ->
            keyUpMap[newValue.keyCode]?.call()
        })
        setProcedure("setKeyUpListener", {
            changeMap(keyUpMap, it)
        })
        setProcedure("clearListeners", {
            clear()
        })
    }

    fun clear() {
        mouseDownMap.clear()
        mouseUpMap.clear()
        keyDownMap.clear()
        keyUpMap.clear()
    }

    private fun changeMap(map: HashMap<Int, LuaFunction>, args: Varargs) {
        if (args.isnil(2)) {
            map.remove(args.checkint(1))
        } else {
            map[args.checkint(1)] = args.checkfunction(2)
        }
    }
}