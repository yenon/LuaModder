package lua.swing

import Main
import org.luaj.vm2.LuaTable
import util.setProcedure
import java.awt.Color
import java.awt.Font
import java.nio.file.Files
import javax.swing.ImageIcon
import javax.swing.JLabel

class LuaLabel(x: Int, y: Int) : LuaTable() {
    val e: JLabel = JLabel()

    init {
        e.background = Color(0, 0, 0, 0)
        e.setLocation(x, y)
        setProcedure("setX", {
            e.setBounds(it.checkint(1), e.y, e.width, e.height)
        })
        setProcedure("setY", {
            e.setBounds(e.x, it.checkint(1), e.width, e.height)
        })
        setProcedure("setWidth", {
            e.setBounds(e.x, e.y, it.checkint(1), e.height)
        })
        setProcedure("setHeight", {
            e.setBounds(e.x, e.y, e.width, it.checkint(1))
        })
        setProcedure("setText", {
            e.text = it.checkjstring(1)
            val fm = e.getFontMetrics(e.font)
            e.setBounds(e.x, e.y, fm.stringWidth(e.text), e.font.size + 2)
        })
        setProcedure("setTextColor", {
            e.foreground = Color(it.checkint(1), it.checkint(2), it.checkint(3))
        })
        setProcedure("setFont", {
            e.font = Font(it.checkjstring(1), e.font.style, e.font.size)
        })
        setProcedure("setFontSize", {
            e.font = Font(e.font.name, e.font.style, it.checkint(1))
        })
        setProcedure("setImage", {
            val icon = ImageIcon(Files.readAllBytes(Main.path.resolve("scripts/${it.checkjstring(1)}")))
            e.setBounds(e.x, e.y, icon.iconWidth, icon.iconHeight)
            e.icon = icon
        })
        setProcedure("setVisible", {
            e.isVisible = it.checkboolean(1)
        })
    }

    fun getNode(): JLabel {
        return e
    }
}
