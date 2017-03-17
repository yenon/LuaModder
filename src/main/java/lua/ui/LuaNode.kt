package lua.ui

import javafx.scene.Parent
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import tornadofx.*

/**
 * Created by basti on 15.03.2017.
 */

abstract class LuaNode<out T : Parent> : LuaTable() {
    abstract val e: T

    init {
        set("setX", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.layoutX = args.checkdouble(1)
                return LuaValue.NIL
            }
        })
        set("setY", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.layoutY = args.checkdouble(1)
                return LuaValue.NIL
            }
        })
        set("setWidth", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.prefWidth(args.checkdouble(1))
                return LuaValue.NIL
            }
        })
        set("setHeight", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.prefWidth(args.checkdouble(1))
                return LuaValue.NIL
            }
        })
        set("remove", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.removeFromParent()
                return LuaValue.NIL
            }
        })
    }

    fun getNode(): Parent {
        return e
    }
}