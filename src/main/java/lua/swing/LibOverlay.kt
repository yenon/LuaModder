package lua.swing

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import util.setFunction
import util.setProcedure
import java.awt.Container

class LibOverlay(x: Int, y: Int, width: Int, height: Int) : LuaTable() {
    var view: TransparentView? = lua.swing.TransparentView(x, y, width, height)
    var root: Container? = view!!.getRoot()

    init {
        setFunction("addLabel", {
            val label: LuaLabel = LuaLabel(it.checkint(1), it.checkint(2))
            root!!.add(label.getNode())
            return@setFunction label
        })
        /*
        setFunction("addImage", {
            val image: LuaImage = LuaImage()
            root!!.add(image.getNode())
            return@setFunction image
        })
        */
        setProcedure("clear", { root!!.removeAll() })
        setProcedure("hide", { view!!.hide() })
        setProcedure("show", { view!!.show() })
        setProcedure("toggle", {
            if (view!!.isShowing()) {
                view!!.hide()
            } else {
                view!!.show()
            }
        })
        setFunction("isShowing", {
            return@setFunction LuaValue.valueOf(view!!.isShowing())
        })
        setProcedure("close", {
            view!!.hide()
            view = null
            root = null
        })
    }
}

