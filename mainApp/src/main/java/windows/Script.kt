package windows

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import lua.LuaExecutor
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CancellationException

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

    var image: SimpleObjectProperty<Image> = SimpleObjectProperty<Image>()
    val name: String
    var luaExecutor: LuaExecutor? = null

    init {
        refreshImage()
        name = path.fileName.toString()
    }

    fun execute() {
        Thread({
            try {
                initExecutor()
                luaExecutor!!.exec(String(Files.readAllBytes(path.resolve("script.lua"))))
            } catch (_: CancellationException) {
            }
        }).start()
    }

    fun stop() {
        luaExecutor?.clear()
    }

    fun isAvailable(): Boolean {
        return Files.exists(path)
    }

    fun refreshImage() {
        val imagePath = path.resolve("image.png")
        if (Files.isRegularFile(imagePath)) {
            image.set(Image(Files.newInputStream(imagePath)))
        } else {
            image.set(defaultImage)
        }
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (luaExecutor?.hashCode() ?: 0)
        return result
    }
}