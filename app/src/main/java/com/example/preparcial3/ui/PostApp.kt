package com.example.preparcial3.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.preparcial3.data.AppContainer
import com.example.preparcial3.data.Post

@Composable
fun PostApp() {
    val navController = rememberNavController()
    val viewModel: PostViewModel = viewModel(
        factory = PostViewModel.factory(AppContainer.repository)
    )

    NavHost(navController = navController, startDestination = "posts") {
        composable("posts") {
            PostListScreen(
                viewModel = viewModel,
                onPostClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable(
            route = "detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("postId") ?: return@composable
            PostDetailScreen(
                post = viewModel.findPost(id),
                onBack = navController::popBackStack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostListScreen(
    viewModel: PostViewModel,
    onPostClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val userFilter by viewModel.userFilter.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Publicaciones") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::updateQuery,
                label = { Text("Buscar por título o contenido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                item {
                    FilterChip(
                        selected = userFilter == null,
                        onClick = { viewModel.updateUserFilter(null) },
                        label = { Text("Todos") }
                    )
                }
                itemsIndexed((1..10).toList()) { _, userId ->
                    FilterChip(
                        selected = userFilter == userId,
                        onClick = { viewModel.updateUserFilter(userId) },
                        label = { Text("Usuario $userId") }
                    )
                }
            }
            when (val current = state) {
                PostsUiState.Loading -> Centered { CircularProgressIndicator() }
                is PostsUiState.Error -> Centered {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(current.message)
                        Button(onClick = viewModel::loadPosts) { Text("Reintentar") }
                    }
                }
                is PostsUiState.Success -> {
                    if (current.posts.isEmpty()) {
                        Centered { Text("No hay resultados") }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(current.posts, key = Post::id) { post ->
                                PostCard(post = post, onClick = { onPostClick(post.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Usuario ${post.userId}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = post.body,
                maxLines = 2,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailScreen(post: Post?, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = { Button(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { padding ->
        if (post == null) {
            Centered(modifier = Modifier.padding(padding)) { Text("Publicación no encontrada") }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(post.title, style = MaterialTheme.typography.headlineSmall)
                Text("Publicación #${post.id} · Usuario ${post.userId}")
                Text(post.body, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun Centered(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}
