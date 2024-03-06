package com.example.posts.data

import com.example.posts.external.BaseClient
import com.example.posts.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostsRepo {

    private val baseClient = BaseClient.create()

    fun getPosts(): Flow<List<Post>> {
        return flow {
            val posts = baseClient.getPosts()
            emit(posts)
        }
    }

    fun updatePost(post: Post): Flow<Post> {
        return flow {
            val updatedPost = baseClient.updatePost(post.id, post)
            emit(updatedPost)
        }
    }
}