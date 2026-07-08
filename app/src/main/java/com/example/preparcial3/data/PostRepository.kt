package com.example.preparcial3.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostRepository(private val api: PostApi) {
    suspend fun getPosts(): List<Post> = api.getPosts()
}

object AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val repository = PostRepository(retrofit.create(PostApi::class.java))
}
