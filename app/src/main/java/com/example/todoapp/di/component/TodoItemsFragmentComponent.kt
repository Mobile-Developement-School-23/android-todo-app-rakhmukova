package com.example.todoapp.di.component

import com.example.todoapp.ui.todolist.TodoItemsFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface TodoItemsFragmentComponent {
    fun inject(fragment: TodoItemsFragment)
}