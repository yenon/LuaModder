package windows

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import lua.LuaExecutor
import lua.ReverseKeyLookup
import lua.ReverseModifierLookup
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import tornadofx.*
import util.HookThread

class WindowDebug : View() {

    class LuaCommand {
        var input: String by property<String>()
        fun inputProperty() = getProperty(LuaCommand::input)
        var output: String by property<String>()
        fun outputProperty() = getProperty(LuaCommand::output)
    }

    val debugExecutor: LuaExecutor = LuaExecutor()
    val commands: ObservableList<LuaCommand> = FXCollections.observableArrayList<LuaCommand>()!!
    val observableLuaInput: SimpleStringProperty = SimpleStringProperty()

    init {
        commands.addListener(ListChangeListener {
            while (it.next()) {
                if (it.wasAdded()) {
                    it.addedSubList.forEach {
                        Thread(Runnable {
                            try {
                                val out: LuaValue = debugExecutor.exec(it.input)

                                if (out.istable()) {
                                    it.output = resolveTable(out.checktable())
                                } else if (out.isnil()) {
                                    it.output = "Done!"
                                } else {
                                    it.output = out.toString()
                                }
                            } catch (error: LuaError) {
                                it.output = error.message!!
                                error.printStackTrace()
                            }
                        }).start()
                    }
                }
            }
        })
    }

    override val root: Parent = tabpane {
        tab("Logging") {
            textarea("logging started.\n") {
                isEditable = false
            }
        }
        tab("Lua Console") {
            vbox {
                label {
                    HookThread.keyDownEventProperty.addListener({ _, _, newValue ->
                        Platform.runLater({
                            text = "${ReverseKeyLookup[newValue.keyCode]}[${newValue.keyCode}] " +
                                    "${ReverseModifierLookup.resolve(newValue.modifiers)}[${newValue.modifiers}]"
                        })
                    })
                }
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
                    textProperty().bindBidirectional(observableLuaInput)
                }
            }
        }
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }

    private fun onLuaExecute() {
        Thread({
            val command: LuaCommand = LuaCommand()
            command.input = observableLuaInput.value
            commands.add(command)
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