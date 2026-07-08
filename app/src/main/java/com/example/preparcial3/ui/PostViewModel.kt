package com.example.preparcial3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.preparcial3.data.Post
import com.example.preparcial3.data.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

sealed interface PostsUiState {
    data object Loading : PostsUiState
    data class Success(val posts: List<Post>) : PostsUiState
    data class Error(val message: String) : PostsUiState
}

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    private val allPosts = MutableStateFlow<List<Post>>(emptyList())
    private val _loading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private val _query = MutableStateFlow("")
    private val _userFilter = MutableStateFlow<Int?>(null)

    val query: StateFlow<String> = _query.asStateFlow()
    val userFilter: StateFlow<Int?> = _userFilter.asStateFlow()

    val uiState: StateFlow<PostsUiState> =
        combine(allPosts, _loading, _error, _query, _userFilter) {
                posts, loading, error, query, userId ->
            when {
                loading -> PostsUiState.Loading
                error != null -> PostsUiState.Error(error)
                else -> PostsUiState.Success(
                    posts.filter { post ->
                        (query.isBlank() ||
                            post.title.contains(query, ignoreCase = true) ||
                            post.body.contains(query, ignoreCase = true)) &&
                            (userId == null || post.userId == userId)
                    }
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostsUiState.Loading
        )

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                allPosts.value = repository.getPosts()
            } catch (exception: Exception) {
                _error.value = exception.message ?: "No se pudieron cargar los datos"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateQuery(value: String) {
        _query.value = value
    }

    fun updateUserFilter(value: Int?) {
        _userFilter.value = value
    }

    fun findPost(id: Int): Post? = allPosts.value.find { it.id == id }

    companion object {
        fun factory(repository: PostRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PostViewModel(repository) as T
            }
    }
}
