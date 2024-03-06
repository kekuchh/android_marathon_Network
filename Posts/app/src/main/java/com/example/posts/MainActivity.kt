package com.example.posts

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.posts.data.PostsRepo
import com.example.posts.model.Post
import com.example.posts.ui.theme.PostsTheme
import com.example.posts.viewmodel.PostViewModel
import com.example.posts.viewmodel.PostViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    val postViewModel: PostViewModel = viewModel(
                        factory = PostViewModelFactory(PostsRepo())
                    )
                    val posts by postViewModel.posts.collectAsState()
                    val selectedPost by postViewModel.selectedPost.collectAsState()
                    val isEditing by postViewModel.isEditing.collectAsState()
                    PostsScreen(
                        posts = posts,
                        selectedPost = selectedPost,
                        isEditing = isEditing,
                        onPostClick = { postViewModel.selectPost(it) },
                        onEditClick = { postViewModel.toggleEditing() },
                        onTitleChange = { postViewModel.updateTitle(it) },
                        onBodyChange = { postViewModel.updateBody(it) },
                        onSaveClick = { postViewModel.savePost() }
                    )
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    posts: List<Post>,
    selectedPost: Post?,
    isEditing: Boolean,
    onPostClick: (Post) -> Unit,
    onEditClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),

            )
        }
    ) {
        Row(Modifier.fillMaxSize()) {
            PostsList(
                posts = posts,
                selectedPost = selectedPost,
                onPostClick = onPostClick
            )
            PostDetails(
                post = selectedPost,
                isEditing = isEditing,
                onEditClick = onEditClick,
                onTitleChange = onTitleChange,
                onBodyChange = onBodyChange,
                onSaveClick = onSaveClick
            )
        }
    }
}

@Composable
fun PostsList(
    posts: List<Post>,
    selectedPost: Post?,
    onPostClick: (Post) -> Unit
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        posts.forEach { post ->
            PostItem(
                post = post,
                isSelected = post == selectedPost,
                onClick = { onPostClick(post) }
            )
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if(isSelected){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(Color(0xFF696969)),
            shape = RoundedCornerShape(10.dp),
        )
        {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = post.title,
                    maxLines = 1
                )
                Text(
                    text = "Post №${post.id}",
                )
            }
        }
    }
    else{
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),

            shape = RoundedCornerShape(10.dp),
        )
        {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Пост №${post.id}",
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = post.title,
                    maxLines = 1
                )

            }
        }
    }

}

@Composable
fun PostDetails(
    post: Post?,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top=40.dp, end=8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.post_details),
            fontWeight = FontWeight.Bold
        )
        if (post != null) {
            if (isEditing) {
                TextField(
                    value = post.title,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TextField(
                    value = post.body,
                    onValueChange = onBodyChange,
                    label = { Text(stringResource(R.string.body)) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.save_button))
                }
            } else {
                Text(
                    text = stringResource(R.string.title),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = post.title,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.body),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = post.body,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.edit_button))
                }
            }
        } else {
            Text(
                text = stringResource(R.string.no_post_selected),
            )
        }
    }
}
