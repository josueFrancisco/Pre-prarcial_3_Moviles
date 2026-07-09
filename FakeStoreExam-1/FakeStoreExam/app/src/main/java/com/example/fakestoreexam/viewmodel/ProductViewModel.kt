package com.example.fakestoreexam.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestoreexam.data.remote.RetrofitInstance
import com.example.fakestoreexam.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository(RetrofitInstance.api)

    var uiState by mutableStateOf(ProductUiState())
        private set

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val products = repository.getProducts()
                val categories = repository.getCategories()
                uiState = uiState.copy(
                    isLoading = false,
                    products = products,
                    categories = listOf("Todas") + categories,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión. Verifica tu internet."
                )
            }
        }
    }

    fun loadProductDetail(id: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, selectedProduct = null)
            try {
                val product = repository.getProductById(id)
                uiState = uiState.copy(isLoading = false, selectedProduct = product)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "No se pudo cargar el detalle del producto."
                )
            }
        }
    }

    fun onSearchChange(value: String) {
        uiState = uiState.copy(searchText = value)
    }

    fun onCategoryChange(value: String) {
        uiState = uiState.copy(selectedCategory = value)
    }
}
