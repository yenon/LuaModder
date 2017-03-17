package windows

import javafx.scene.image.Image
import lua.LuaExecutor
import java.nio.file.Files
import java.nio.file.Path

/**
 * Created by basti on 14.03.2017.
 */
class Script(val path: Path) {
    override fun equals(other: Any?): Boolean {
        if (other is Script) {
            val otherScript: Script = other
            return otherScript.path.toString() == this.path.toString()
        }
        return false
    }

    companion object {
        val defaultImage = Image(Script::class.java.getResourceAsStream("/icon.png"))
    }

    fun initExecutor() {
        if (luaExecutor == null) {
            luaExecutor = LuaExecutor()
        }
    }

    val image: Image
    val name: String
    var luaExecutor: LuaExecutor? = null

    init {
        val imagePath = path.resolve("image.png")
        if (Files.isRegularFile(imagePath)) {
            image = Image(Files.newInputStream(imagePath))
        } else {
            image = defaultImage
        }
        name = path.fileName.toString()
    }

    fun execute() {
        initExecutor()
        luaExecutor?.exec(path.resolve("script.lua"))
    }

    fun stop() {
        luaExecutor?.clear()
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (luaExecutor?.hashCode() ?: 0)
        return result
    }
}