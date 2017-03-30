package test

import javafx.application.Application
import lua.LuaExecutor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.luaj.vm2.LuaNumber
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import util.HookThread

class LuaTest {

    val executor: LuaExecutor = LuaExecutor()

    @Test
    fun testLua() {
        assertEquals(executor.exec("return \"Hello World!\"").toString(), "Hello World!")
        assertEquals(executor.exec("return 1+1"), LuaNumber.valueOf(2))
    }

    @Test
    fun testIOLib() {
        HookThread.start()
        Thread.sleep(500)
        assertEquals((executor.exec("io.moveAbsolute(400,0)\n" +
                "io.moveRelative(0,300)\n" +
                "return {io.getMousePos()}") as LuaTable).unpack().toString().replace(" ", ""),
                LuaValue.varargsOf(LuaValue.valueOf(400), LuaValue.valueOf(300)).toString().replace(" ", ""))
    }

    @Test
    fun testScreenLib() {
        object : Thread() {
            override fun run() {
                Application.launch(TestTestMain::class.java, null)
            }
        }.start()
        Thread.sleep(4000)
        assertEquals((executor.exec("screen = wrapScreen(0)\n" +
                "return {screen.pixelAt(8,8)}") as LuaTable).unpack().toString().replace(" ", ""),
                LuaValue.varargsOf(LuaValue.valueOf(192), LuaValue.valueOf(128), LuaValue.valueOf(64))
                        .toString().replace(" ", ""))
    }
}