package util

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.MultipleSelectionModel

internal class DisabledSelectionModel<T> : MultipleSelectionModel<T>() {
    init {
        super.setSelectedIndex(-1)
        super.setSelectedItem(null)
    }

    override fun getSelectedIndices(): ObservableList<Int> {
        return FXCollections.emptyObservableList<Int>()
    }

    override fun getSelectedItems(): ObservableList<T> {
        return FXCollections.emptyObservableList<T>()
    }

    override fun selectAll() {}
    override fun selectFirst() {}
    override fun selectIndices(index: Int, vararg indicies: Int) {}
    override fun selectLast() {}
    override fun clearAndSelect(index: Int) {}
    override fun clearSelection() {}
    override fun clearSelection(index: Int) {}
    override fun isEmpty(): Boolean {
        return true
    }

    override fun isSelected(index: Int): Boolean {
        return false
    }

    override fun select(index: Int) {}
    override fun select(item: T) {}
    override fun selectNext() {}
    override fun selectPrevious() {}

}