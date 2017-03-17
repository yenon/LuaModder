package lua.ui

import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs

/**
 * Created by basti on 15.03.2017.
 */
class LuaLabel : LuaNode<Label>() {
    override val e: Label = Label()

    init {
        set("setText", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.text = args.checkjstring(1)
                return LuaValue.NIL
            }
        })
        set("setTextColor", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.textFill = Color.rgb(args.checkint(1), args.checkint(2), args.checkint(3))
                return LuaValue.NIL
            }
        })
        set("setFont", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.font = Font.font(args.checkjstring(1), e.font.size)
                return LuaValue.NIL
            }
        })
        set("setFontSize", object : FXVarArgFunction() {
            override fun run(args: Varargs): Varargs {
                e.font = Font.font(e.font.family, args.checkdouble(1))
                return LuaValue.NIL
            }
        })
    }
}
