package lua.ui

import javafx.application.Platform
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask

/**
 * Created by basti on 15.03.2017.
 */

abstract class FXVarArgFunction : VarArgFunction() {
    abstract fun run(args: Varargs): Varargs

    override fun invoke(varargs: Varargs): Varargs {
        val task: FutureTask<Varargs> = FutureTask(Callable<Varargs> {
            return@Callable run(varargs)
        })
        Platform.runLater(task)
        return task.get()
    }
}