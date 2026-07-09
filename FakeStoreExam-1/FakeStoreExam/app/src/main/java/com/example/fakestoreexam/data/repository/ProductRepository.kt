package com.example.fakestoreexam.data.repository

import com.example.fakestoreexam.data.model.Product
import com.example.fakestoreexam.data.remote.ApiService

class ProductRepository(
    private val api: ApiService
) {
    suspend fun getProducts(): List<Product> = api.getProducts()

    suspend fun getProductById(id: Int): Product = api.getProductById(id)

    suspend fun getCategories(): List<String> = api.getCategories()
}
