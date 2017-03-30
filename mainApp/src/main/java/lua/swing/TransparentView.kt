package lua.swing

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import org.luaj.vm2.LuaTable
import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel

class TransparentView(x: Int, y: Int, width: Int, height: Int) : LuaTable() {

    val frame: JFrame = JFrame("Transparent Window")
    val panel: JPanel = JPanel()

    private fun getHWnd(w: Component): WinDef.HWND {
        val hwnd = WinDef.HWND()
        hwnd.pointer = Native.getComponentPointer(w)
        return hwnd
    }

    init {
        panel.layout = null
        panel.background = Color(0, 0, 0, 0)
        panel.setLocation(0, 0)
        panel.size = Dimension(width, height)

        frame.setLocation(x, y)
        frame.size = Dimension(width, height)
        //frame.extendedState = JFrame.MAXIMIZED_BOTH
        frame.isUndecorated = true
        frame.background = Color(0, 0, 0, 0)
        frame.isAlwaysOnTop = true
        // Without this, the window is draggable from any non transparent
        // point, including points  inside textboxes.
        frame.rootPane.putClientProperty("apple.awt.draggableWindowBackground", false)
        frame.add(AlphaContainer(panel))

        frame.isVisible = true

        if (System.getProperty("os.name").startsWith("Win")) {
            val hwnd = getHWnd(frame)
            var wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE)
            wl = wl or WinUser.WS_EX_LAYERED or WinUser.WS_EX_TRANSPARENT
            println(wl)
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl)
        }
    }

    fun getRoot(): Container {
        return panel
    }

    fun hide() {
        frame.isVisible = false
    }

    fun isShowing(): Boolean {
        return frame.isVisible
    }

    fun show() {
        frame.isVisible = true
    }
}