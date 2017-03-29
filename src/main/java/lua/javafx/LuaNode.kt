package lua.javafx

import javafx.scene.Node
import org.luaj.vm2.LuaBoolean
import org.luaj.vm2.LuaTable
import tornadofx.*
import util.setFXFunction
import util.setFXProcedure

abstract class LuaNode<out T : Node> : LuaTable() {
    protected abstract val e: T

    init {
        setFXProcedure("setX", {
            e.layoutX = it.checkdouble(1)
        })
        setFXProcedure("setY", {
            e.layoutY = it.checkdouble(1)
        })
        setFXProcedure("setWidth", {
            e.prefWidth(it.checkdouble(1))
        })
        setFXProcedure("setHeight", {
            e.prefWidth(it.checkdouble(1))
        })
        setFXProcedure("remove", {
            e.removeFromParent()
        })
        setFXProcedure("setVisible", {
            e.isVisible = it.checkboolean(1)
        })
        setFXFunction("isVisible", {
            return@setFXFunction LuaBoolean.valueOf(e.isVisible)
        })
    }

    fun getNode(): Node {
        return e
    }


}