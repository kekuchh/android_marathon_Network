package com.example.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.posts.data.PostsRepo
import com.example.posts.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PostViewModel(private val postsRepo: PostsRepo) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _selectedPost = MutableStateFlow<Post?>(null)
    private val _isEditing = MutableStateFlow(false)
    
    val posts: StateFlow<List<Post>> = _posts    
    val selectedPost: StateFlow<Post?> = _selectedPost    
    val isEditing: StateFlow<Boolean> = _isEditing

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            val posts = postsRepo.getPosts().first()
            _posts.value = posts
        }
    }

    fun selectPost(post: Post) {
        _selectedPost.value = post
        _isEditing.value = false
    }

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value
    }

    fun updateTitle(title: String) {
        _selectedPost.value = _selectedPost.value?.copy(title = title)
    }

    fun updateBody(body: String) {
        _selectedPost.value = _selectedPost.value?.copy(body = body)
    }

    fun savePost() {
        val post = _selectedPost.value
        if (post != null) {
            viewModelScope.launch {
                val updatedPost = postsRepo.updatePost(post).first()
                _selectedPost.value = updatedPost
                _isEditing.value = false
            }
        }
    }
}
