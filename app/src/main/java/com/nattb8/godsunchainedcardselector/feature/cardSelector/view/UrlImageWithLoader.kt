package com.nattb8.godsunchainedcardselector.feature.cardSelector.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

/**
 * Creates a composable image based on the given [url].
 * While loading the image, a progress indicator will be shown.
 *
 * @param url the url of the image to load
 * @param contentDescription text used by accessibility services to describe what this image is
 * @param modifier used to adjust the layout or draw decoration content
 */
@ExperimentalCoilApi
@Composable
fun UrlImageWithLoader(
    url: String, contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center) {
        val painter = rememberImagePainter(url)
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
        if (painter.state is ImagePainter.State.Loading)
            CircularProgressIndicator()
    }
}