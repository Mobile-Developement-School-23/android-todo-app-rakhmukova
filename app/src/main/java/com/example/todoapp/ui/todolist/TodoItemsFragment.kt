package com.example.todoapp.ui.todolist

import com.example.todoapp.viewmodel.todolist.TodoListViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.TodoApp
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.databinding.FragmentTodoItemsBinding
import com.example.todoapp.ui.additem.AddTodoItemFragment
import kotlinx.coroutines.flow.collectLatest


class TodoItemsFragment : Fragment(), TodoItemChangeCallbacks {

    private lateinit var todoAdapter: TodoItemsAdapter

    private val todoListViewModel: TodoListViewModel by lazy {
        (requireActivity().application as TodoApp).todoListViewModel
    }

    private lateinit var binding: FragmentTodoItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupCreateTaskButton()
        setupVisibilityToggleButton()
    }

    private fun setupViewModel() {
        lifecycleScope.launchWhenStarted {
            todoListViewModel.filteredTodoItems.collectLatest {
                binding.completedTitle.text = String.format(getString(R.string.completed_tasks),
                    todoListViewModel.countCompletedTasks())
            }
        }

        binding.visibilityToggleButton.isChecked = !todoListViewModel.showOnlyCompletedTasks.value
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoItemsAdapter(this)
        binding.todoItems.adapter = todoAdapter
        binding.todoItems.layoutManager = LinearLayoutManager(requireContext())
        binding.todoItems.addItemDecoration(TodoItemsOffsetItemDecoration(bottomOffset = 16f.toInt()))

        lifecycleScope.launchWhenStarted {
            todoListViewModel.todoItems.collectLatest { list ->
                todoAdapter.submitList(list)
            }
        }

        lifecycleScope.launchWhenStarted {
            todoListViewModel.filteredTodoItems.collectLatest { filteredList ->
                todoAdapter.submitList(filteredList)
            }
        }
    }

    private fun setupCreateTaskButton() {
        binding.createTaskButton.setOnClickListener {
            findNavController().navigate(R.id.action_TodoItemsFragment_to_AddItemFragment)
        }
    }

    private fun setupVisibilityToggleButton() {
        binding.visibilityToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                todoListViewModel.setShowOnlyCompletedTasks(false)
                todoListViewModel.filterTodoItems(false)
            } else {
                todoListViewModel.setShowOnlyCompletedTasks(true)
                todoListViewModel.filterTodoItems(true)
            }
        }
    }

    override fun onTodoItemClicked(todoItem: TodoItem) {
        val args = bundleOf(AddTodoItemFragment.ARG_TODO_ITEM_ID to todoItem.id)
        findNavController().navigate(R.id.action_TodoItemsFragment_to_AddItemFragment, args)
    }

    override fun onTodoItemCheckedChanged(todoItem: TodoItem, isChecked: Boolean) {
        todoListViewModel.updateChecked(todoItem, isChecked)
    }
}