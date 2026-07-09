package com.example.fakestoreexam.data.remote

import com.example.fakestoreexam.data.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product

    @GET("products/categories")
    suspend fun getCategories(): List<String>
}
