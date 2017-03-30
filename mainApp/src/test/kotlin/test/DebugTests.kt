package test

import org.junit.Test
import java.awt.Color
import javax.swing.JFrame
import javax.swing.JTextField

class DebugTests {

    @Test
    fun debug() {
        Thread({
            val frame = JFrame("Transparent Window")
            frame.isUndecorated = true
            frame.background = Color(0, 0, 0, 0)
            frame.isAlwaysOnTop = true
            // Without this, the window is draggable from any non transparent
            // point, including points  inside textboxes.
            frame.rootPane.putClientProperty("apple.awt.draggableWindowBackground", false)

            frame.contentPane.layout = java.awt.BorderLayout()
            frame.contentPane.add(JTextField("text field north"), java.awt.BorderLayout.NORTH)
            frame.contentPane.add(JTextField("text field south"), java.awt.BorderLayout.SOUTH)
            frame.isVisible = true
            frame.pack()
        }).start()
    }
}