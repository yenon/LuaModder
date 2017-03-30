import javafx.application.Platform
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask

fun BufferedImage.scale(factor: Double): BufferedImage {
    val created: BufferedImage = BufferedImage((this.width * factor).toInt(), (this.height * factor).toInt(), this.type)
    created.createGraphics().drawRenderedImage(this, AffineTransform.getScaleInstance(factor, factor))
    return created
}

fun LuaTable.setFXFunction(name: String, factory: (Varargs) -> Varargs) {
    set(name, object : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            val task: FutureTask<Varargs> = FutureTask(Callable<Varargs> {
                return@Callable factory.invoke(args)
            })
            Platform.runLater(task)
            return task.get()
        }
    })
}

fun LuaTable.setFXProcedure(name: String, factory: (Varargs) -> Unit) {
    set(name, object : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            val task: FutureTask<Varargs> = FutureTask(Callable<Varargs> {
                factory.invoke(args)
                return@Callable LuaValue.NIL
            })
            Platform.runLater(task)
            return task.get()
        }
    })
}

fun LuaTable.setFunction(name: String, factory: (Varargs) -> Varargs) {
    set(name, object : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            return factory.invoke(args)
        }
    })
}

fun LuaTable.setProcedure(name: String, factory: (Varargs) -> Unit) {
    set(name, object : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            factory.invoke(args)
            return LuaValue.NIL
        }
    })
}