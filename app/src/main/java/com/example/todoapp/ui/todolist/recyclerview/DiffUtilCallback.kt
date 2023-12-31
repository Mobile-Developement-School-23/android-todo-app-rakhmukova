package com.example.todoapp.ui.todolist.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.data.model.TodoItem

/**
 * Callback class for calculating the difference between two lists of items.
 */
class DiffUtilCallback : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}
