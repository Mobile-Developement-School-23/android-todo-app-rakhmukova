package com.example.todoapp.di.module

import androidx.lifecycle.ViewModel
import com.example.todoapp.ui.main.MainViewModel
import com.example.todoapp.ui.settings.SettingsViewModel
import com.example.todoapp.ui.todolist.TodoListViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

/**
 * Dagger module for providing view model-related dependencies.
 */
@Module
interface ViewModelModule {
    @Binds
    @[IntoMap ViewModelKey(MainViewModel::class)]
    fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(TodoListViewModel::class)]
    fun bindTodoListViewModel(todoListViewModel: TodoListViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(SettingsViewModel::class)]
    fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel
}
