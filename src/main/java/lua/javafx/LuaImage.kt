package lua.javafx

import Main
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.luaj.vm2.Varargs
import util.setFXProcedure
import java.nio.file.Files

class LuaImage : LuaNode<ImageView>() {
    override val e: ImageView = ImageView()

    init {
        e.isMouseTransparent = true
        e.isFocusTraversable = false

        setFXProcedure("setImage", { args: Varargs ->
            e.image = Image(Files.newInputStream(Main.path.resolve("scripts/${args.checkjstring(1)}")))
        })
    }
}