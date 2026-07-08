package com.example.preparcial3.data

import retrofit2.http.GET

interface PostApi {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}
