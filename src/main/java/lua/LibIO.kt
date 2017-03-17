package lua

import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseWheelEvent
import org.luaj.vm2.*
import org.luaj.vm2.lib.VarArgFunction
import util.HookThread
import java.awt.MouseInfo
import java.awt.Point

class LibIO : LuaTable() {

    val mouseDownMap: HashMap<Int, LuaFunction> = HashMap()
    val mouseUpMap: HashMap<Int, LuaFunction> = HashMap()
    val keyDownMap: HashMap<Int, LuaFunction> = HashMap()
    val keyUpMap: HashMap<Int, LuaFunction> = HashMap()

    init {
        set("pressKey", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                GlobalScreen.postNativeEvent(NativeKeyEvent(
                        NativeKeyEvent.NATIVE_KEY_PRESSED,
                        0,
                        0,
                        args.checkint(1),
                        NativeKeyEvent.CHAR_UNDEFINED))
                return LuaValue.NIL
            }
        })
        set("releaseKey", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                GlobalScreen.postNativeEvent(NativeKeyEvent(
                        NativeKeyEvent.NATIVE_KEY_RELEASED,
                        0,
                        0,
                        args.checkint(1),
                        NativeKeyEvent.CHAR_UNDEFINED))
                return LuaValue.NIL
            }
        })

        set("pressMouse", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val location: Point = MouseInfo.getPointerInfo().location
                GlobalScreen.postNativeEvent(NativeMouseEvent(
                        NativeMouseEvent.NATIVE_MOUSE_PRESSED,
                        0,
                        optint(2, location.x),
                        optint(3, location.y),
                        0,
                        args.checkint(1)
                ))
                return LuaValue.NIL
            }
        })
        set("releaseMouse", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val location: Point = MouseInfo.getPointerInfo().location

                GlobalScreen.postNativeEvent(NativeMouseEvent(
                        NativeMouseEvent.NATIVE_MOUSE_RELEASED,
                        0,
                        optint(2, location.x),
                        optint(3, location.y),
                        0,
                        args.checkint(1)
                ))
                return LuaValue.NIL
            }
        })
        set("moveAbsolute", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                GlobalScreen.postNativeEvent(NativeMouseEvent(
                        NativeMouseEvent.NATIVE_MOUSE_MOVED,
                        0, //Modifiers
                        args.checkint(1),
                        args.checkint(2),
                        0, //Clickcount
                        NativeMouseEvent.NOBUTTON
                ))
                return LuaValue.NIL
            }
        })
        set("moveRelative", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val location: Point = MouseInfo.getPointerInfo().location
                GlobalScreen.postNativeEvent(NativeMouseEvent(
                        NativeMouseEvent.NATIVE_MOUSE_MOVED,
                        0,
                        location.x + args.checkint(1),
                        location.y + args.checkint(2),
                        0,
                        NativeMouseEvent.NOBUTTON
                ))

                return LuaValue.NIL
            }
        })
        set("scroll", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                GlobalScreen.postNativeEvent(NativeMouseWheelEvent(
                        NativeMouseWheelEvent.NATIVE_MOUSE_WHEEL,
                        0, //Modifiers
                        0, //x
                        0, //y
                        0, //Click count
                        NativeMouseWheelEvent.WHEEL_BLOCK_SCROLL,
                        0, //Unit rotation
                        args.checkint(1), //"Click" rotation
                        if (args.optboolean(2, true)) {
                            NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION
                        } else {
                            NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION
                        }
                ))
                return LuaValue.NIL
            }
        })
        set("getMousePos", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val location: Point = MouseInfo.getPointerInfo().location
                return LuaValue.varargsOf(LuaNumber.valueOf(location.x), LuaNumber.valueOf(location.y))
            }
        })
        HookThread.mouseDownEventProperty.addListener({ _, _, newValue ->
            mouseDownMap[newValue.button]?.call()
        })
        set("setMouseDownListener", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                changeMap(mouseDownMap, args)
                return LuaValue.NIL
            }
        })
        HookThread.mouseUpEventProperty.addListener({ _, _, newValue ->
            mouseUpMap[newValue.button]?.call()
        })
        set("setMouseUpListener", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                changeMap(mouseUpMap, args)
                return LuaValue.NIL
            }
        })
        HookThread.keyDownEventProperty.addListener({ _, _, newValue ->
            keyDownMap[newValue.keyCode]?.call()
        })
        set("setKeyDownListener", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                changeMap(keyDownMap, args)
                return LuaValue.NIL
            }
        })
        HookThread.keyUpEventProperty.addListener({ _, _, newValue ->
            keyUpMap[newValue.keyCode]?.call()
        })
        set("setKeyUpListener", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                changeMap(keyUpMap, args)
                return LuaValue.NIL
            }
        })
        set("clearListeners", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                clear()
                return LuaValue.NIL
            }
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