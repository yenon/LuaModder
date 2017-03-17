package windows

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import lua.LuaExecutor
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import tornadofx.*

/**
 * Created by yenon on 2/28/17.
 */
class WindowDebug : View() {

    class LuaCommand {
        var input: String by property<String>()
        fun inputProperty() = getProperty(LuaCommand::input)
        var output: String by property<String>()
        fun outputProperty() = getProperty(LuaCommand::output)
    }

    val debugExecutor: LuaExecutor = LuaExecutor()
    val commands: ObservableList<LuaCommand> = FXCollections.observableArrayList<LuaCommand>()!!
    val observeableLuaInput: SimpleStringProperty = SimpleStringProperty()

    override val root: Parent = tabpane {
        tab("Logging") {
            textarea("logging started.\n") {
                isEditable = false
            }
        }
        tab("Lua Console") {
            vbox {
                tableview(commands) {
                    column("Command", LuaCommand::inputProperty)
                    column("Output", LuaCommand::outputProperty)
                    columnResizePolicy = SmartResize.POLICY
                }
                textarea {
                    onKeyPressed = EventHandler<KeyEvent> {
                        if (it.code == KeyCode.ENTER && it.isShiftDown) {
                            onLuaExecute()
                        }
                    }
                    promptText = "Press shift+enter to execute"
                    textProperty().bindBidirectional(observeableLuaInput)
                }
            }
        }
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }

    private fun onLuaExecute() {
        Thread(Runnable {
            val command: LuaCommand = LuaCommand()
            command.input = observeableLuaInput.value
            try {
                val out: LuaValue = debugExecutor.exec(command.input)

                if (out.istable()) {
                    command.output = resolveTable(out.checktable())
                } else if (out.isnil()) {
                    command.output = "Done!"
                } else {
                    command.output = out.toString()
                }
            } catch (error: LuaError) {
                command.output = error.message!!
                error.printStackTrace()
            }
            Platform.runLater(Runnable {
                commands.add(command)
            })
        }).start()
    }

    private fun luaValueToString(value: LuaValue): String {
        if (value.isstring()) {
            return "\"${value.checkstring()}\""
        } else if (value.istable()) {
            return resolveTable(value as LuaTable)
        } else {
            return value.toString()
        }
    }

    private fun resolveTable(table: LuaTable): String {
        val builder: StringBuilder = StringBuilder("{")
        for (key in table.keys()) {
            println("$key -> ${table.get(key)}")
            if (key.isstring() && !key.isnumber()) {
                builder.append("[\"").append(key.checkstring()).append("\"]=").append(luaValueToString(table.get(key)))
            } else {
                builder.append("[$key]=").append(luaValueToString(table.get(key)))
            }
            builder.append(',')
        }
        if (builder[builder.lastIndex] == ',') {
            builder.deleteCharAt(builder.lastIndex)
        }
        builder.append("}")
        return builder.toString()
    }
}