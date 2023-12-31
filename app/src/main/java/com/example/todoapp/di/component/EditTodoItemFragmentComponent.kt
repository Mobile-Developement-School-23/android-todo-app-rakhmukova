package com.example.todoapp.di.component

import com.example.todoapp.di.scope.FragmentScope
import com.example.todoapp.ui.edititem.EditTodoItemFragment
import dagger.Subcomponent

/**
 * Dagger subcomponent for dependencies related to the EditTodoItemFragment.
 */
@FragmentScope
@Subcomponent
interface EditTodoItemFragmentComponent {
    fun inject(fragment: EditTodoItemFragment)
}
