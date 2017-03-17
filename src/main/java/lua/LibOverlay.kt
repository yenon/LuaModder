package lua

import javafx.scene.layout.AnchorPane
import lua.ui.LuaLabel
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import tornadofx.*
import windows.TransparentView

/**
 * Created by basti on 15.03.2017.
 */
class LibOverlay(x: Int, y: Int, width: Int, height: Int) : LuaTable() {
    val view: TransparentView = TransparentView.open(x, y, width, height)
    val root: AnchorPane = view.getRoot()

    init {
        set("addLabel", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val label: LuaLabel = LuaLabel()
                root.add(label.getNode())
                return label
            }
        })
        set("clear", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                root.getChildList()?.clear()
                return LuaValue.NIL
            }
        })
        set("hide", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                view.hide()
                return LuaValue.NIL
            }
        })
        set("show", object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                view.show()
                return LuaValue.NIL
            }
        })
    }
}
