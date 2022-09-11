@file:OptIn(
    ExperimentalUnitApi::class,
    ExperimentalPagerApi::class,
    ExperimentalComposeUiApi::class
)

package io.prashant.topalbums.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.prashant.topalbums.R
import io.prashant.topalbums.domain.model.Album
import io.prashant.topalbums.domain.model.AlbumImage
import io.prashant.topalbums.domain.repository.FakeAlbumRepositoryImpl
import io.prashant.topalbums.ui.theme.GREEN
import io.prashant.topalbums.ui.theme.TopAlbumsAssignmentTheme
import io.prashant.topalbums.util.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AlbumScreen(navHostController: NavHostController, albumViewModel: AlbumViewModel) {

    val searchAlbumText = albumViewModel.searchTextState.collectAsState("")
    val favoriteAlbumState = albumViewModel.albumsFavoriteState.collectAsState(false)
    val albumResult by albumViewModel.albumsState.collectAsState(ApiResult.loading(true))
    val isLoading =
        if (albumResult is ApiResult.Loading) (albumResult as ApiResult.Loading).isLoading else false
    val error =
        if (albumResult is ApiResult.Failure) (albumResult as ApiResult.Failure).exception else null
    val albums =
        if (albumResult is ApiResult.Success) (albumResult as ApiResult.Success<List<Album>>).value else emptyList()

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()


    Scaffold(scaffoldState = scaffoldState) {

        LaunchedEffect(Unit) {
            albumViewModel.loadAlbums(searchAlbumText.value)
        }

        LaunchedEffect(error) {
            error?.let {
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Failed to load Albums!"
                    )
                }
            }
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isLoading),
            onRefresh = {
                albumViewModel.loadAlbums("", true)
            },
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val (searchBoxRef, pagerRef, bottomInfoRef) = createRefs()
                SearchBox(
                    selfReference = searchBoxRef,
                    searchTextState = searchAlbumText,
                    onSearchTextChanged = { searchText ->
                        albumViewModel.loadAlbums(searchText, false)
                    },
                    onSearchClosed = {
                        albumViewModel.loadAlbums("", false)
                    }
                )

                AlbumImagePager(
                    selfReference = pagerRef,
                    topReference = searchBoxRef,
                    pagerState = pagerState,
                    favoriteAlbumState = favoriteAlbumState,
                    albums = albums,
                    onFavoriteClick = { albumId, isFavorite ->
                        albumViewModel.setAlbumFavorite(albumId, isFavorite)
                    })

                AlbumInfo(
                    selfReference = bottomInfoRef,
                    isLoading = isLoading,
                    pagerState = pagerState,
                    albums = albums,
                    onAlbumInfoLoad = { albumId ->
                        albumViewModel.isAlbumFavorite(albumId)
                    }
                )
            }
        }

    }
}


@Composable
private fun ConstraintLayoutScope.AlbumImagePager(
    selfReference: ConstrainedLayoutReference,
    topReference: ConstrainedLayoutReference,
    pagerState: PagerState,
    favoriteAlbumState: State<Boolean>,
    albums: List<Album>,
    onFavoriteClick: (String, Boolean) -> Unit
) {

    val imgModifier: Modifier
    val imgScale: ContentScale
    val screenSize: Int

    if (isScreenPortrait()) {
        imgModifier = Modifier
            .fillMaxWidth(0.85f)
        imgScale = ContentScale.FillWidth
        screenSize = getScreenWidthInPixels()
    } else {
        /*imgModifier = Modifier
            .fillMaxHeight(0.85f)
        imgScale = ContentScale.FillHeight
        screenSize = getScreenHeightInPixels()*/

        imgModifier = Modifier
            .fillMaxWidth(0.85f)
        imgScale = ContentScale.FillWidth
        screenSize = getScreenHeightInPixels()
    }

    HorizontalPager(
        count = albums.size,
        state = pagerState,
        modifier = Modifier.constrainAs(selfReference) {
            linkTo(
                top = topReference.bottom,
                bottom = parent.bottom,
                topMargin = 16.dp,
                bottomMargin = 16.dp,
                bias = 0.2f
            )
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            this.verticalChainWeight = 0.3f
        }

    ) { pageNo ->
        ConstraintLayout {

            val (albumImgRef, favoriteBtnRef) = createRefs()

            val albumImage = getAlbumImageAccordingToScreenSize(
                albumImages = albums[pageNo].images,
                screenSize = screenSize
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(albumImage.url)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.album_placeholder),
                contentDescription = "album",
                contentScale = imgScale,
                modifier = imgModifier
                    .constrainAs(albumImgRef) {
                        linkTo(start = parent.start, end = parent.end)
                        top.linkTo(parent.top)
                    }
            )

            IconButton(
                modifier = Modifier
                    .constrainAs(favoriteBtnRef) {
                        top.linkTo(albumImgRef.top, margin = 8.dp)
                        end.linkTo(albumImgRef.end, margin = 8.dp)
                    },
                onClick = {
                    onFavoriteClick(albums[pageNo].id, !favoriteAlbumState.value)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    tint = if (favoriteAlbumState.value) Color.Red else Color.Gray,
                    contentDescription = "favorite",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp)
                )
            }
        }
    }
}


@Composable
private fun ConstraintLayoutScope.AlbumInfo(
    selfReference: ConstrainedLayoutReference,
    isLoading: Boolean,
    pagerState: PagerState,
    albums: List<Album>,
    onAlbumInfoLoad: (String) -> Unit
) {
    val currAlbum = if (albums.isEmpty()) Album() else albums[pagerState.currentPage]
    if (currAlbum.id.isNotEmpty()) {
        onAlbumInfoLoad(currAlbum.id)
    }
    Column(
        modifier = Modifier
            .constrainAs(selfReference) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .fillMaxWidth(0.85f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Album Name
        Text(
            text = currAlbum.name,
            fontSize = TextUnit(25.0f, TextUnitType.Sp),
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 0.dp)
                .align(Alignment.Start)
                .loadingAnimation(isLoading),
            fontWeight = FontWeight.SemiBold,
            color = GREEN,
        )

        // Artist Name
        val artistText =
            if (currAlbum.artist.isBlank()) "" else "by ${currAlbum.artist}"
        Text(
            text = artistText,
            fontSize = TextUnit(15.0f, TextUnitType.Sp),
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 0.dp)
                .align(Alignment.Start)
                .loadingAnimation(isLoading),
            fontWeight = FontWeight.SemiBold,
            color = GREEN,
        )

        // Price
        val priceText =
            if (currAlbum.price.isBlank()) "" else "Price: ${currAlbum.price}"
        Text(
            text = priceText,
            fontSize = TextUnit(15.0f, TextUnitType.Sp),
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .align(Alignment.Start)
                .loadingAnimation(isLoading),
            fontWeight = FontWeight.Light,
            color = GREEN,
        )
    }
}


@Composable
fun ConstraintLayoutScope.SearchBox(
    selfReference: ConstrainedLayoutReference,
    searchTextState: State<String>,
    onSearchTextChanged: (String) -> Unit = {},
    onSearchClosed: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = searchTextState.value,
        onValueChange = { value ->
            onSearchTextChanged(value)
        },
        placeholder = {
            Text(text = "Search Album")
        },
        modifier = Modifier
            .constrainAs(selfReference) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (searchTextState.value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchClosed()
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
        colors = TextFieldDefaults.textFieldColors(
            textColor = GREEN,
            cursorColor = GREEN,
            leadingIconColor = GREEN,
            trailingIconColor = GREEN,
            backgroundColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
    )
}

@Composable
private fun Modifier.loadingAnimation(isVisible: Boolean) =
    placeholder(
        visible = isVisible,
        color = Color.Gray,
        shape = RoundedCornerShape(4.dp),
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = Color.White,
        ),
    )

@Composable
private fun isScreenPortrait() = LocalConfiguration.current.orientation == 1

@Composable
private fun getScreenHeightInPixels(): Int {
    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f
    return (configuration.screenHeightDp.toFloat() * screenDensity).toInt()
}

@Composable
private fun getScreenWidthInPixels(): Int {
    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f
    return (configuration.screenHeightDp.toFloat() * screenDensity).toInt()
}

@Composable
private fun getAlbumImageAccordingToScreenSize(
    albumImages: List<AlbumImage>,
    screenSize: Int
): AlbumImage {
    val maxImageSize = (screenSize * 0.85f).toInt()
    val albumSortedByHeight = albumImages.sortedByDescending { it.height }
    return albumSortedByHeight.find { maxImageSize <= it.height } ?: albumSortedByHeight.first()
}

@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    TopAlbumsAssignmentTheme {
        AlbumScreen(
            navHostController = rememberNavController(),
            albumViewModel = AlbumViewModel(FakeAlbumRepositoryImpl())
        )
    }
}