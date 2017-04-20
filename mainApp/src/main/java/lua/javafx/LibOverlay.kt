package lua.javafx

import javafx.scene.layout.AnchorPane
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import tornadofx.*
import util.setFXFunction
import util.setFXProcedure

class LibOverlay(x: Int, y: Int, width: Int, height: Int) : LuaTable() {
    var view: TransparentView? = TransparentView.open(x, y, width, height)
    var root: AnchorPane? = view!!.getRoot()

    init {
        setFXFunction("addLabel", {
            val label: LuaLabel = LuaLabel(it.checkint(1), it.checkint(2))
            root!!.add(label.getNode())
            return@setFXFunction label
        })
        setFXFunction("addImage", {
            val image: LuaImage = LuaImage(it.checkint(1), it.checkint(2))
            root!!.add(image.getNode())
            return@setFXFunction image
        })
        setFXProcedure("clear", { root!!.getChildList()?.clear() })
        setFXProcedure("hide", { view!!.hide() })
        setFXProcedure("show", { view!!.show() })
        setFXProcedure("toggle", {
            if (view!!.isShowing()) {
                view!!.hide()
            } else {
                view!!.show()
            }
        })
        setFXFunction("isShowing", {
            return@setFXFunction LuaValue.valueOf(view!!.isShowing())
        })
        setFXProcedure("close", {
            view!!.hide()
            view = null
            root = null
        })
    }
}
