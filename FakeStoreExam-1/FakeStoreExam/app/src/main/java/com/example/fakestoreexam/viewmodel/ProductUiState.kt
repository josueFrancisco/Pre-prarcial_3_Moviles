package com.example.fakestoreexam.viewmodel

import com.example.fakestoreexam.data.model.Product

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    val categories: List<String> = emptyList(),
    val searchText: String = "",
    val selectedCategory: String = "Todas",
    val errorMessage: String? = null
) {
    val filteredProducts: List<Product>
        get() = products.filter { product ->
            val matchesSearch = product.title.contains(searchText, ignoreCase = true)
            val matchesCategory = selectedCategory == "Todas" || product.category == selectedCategory
            matchesSearch && matchesCategory
        }
}
