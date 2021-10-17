package com.nattb8.godsunchainedcardselector.feature.cardSelector

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.imePadding
import com.nattb8.godsunchainedcardselector.R
import com.nattb8.godsunchainedcardselector.domain.model.Card
import com.nattb8.godsunchainedcardselector.feature.cardSelector.view.GenericErrorDialog
import com.nattb8.godsunchainedcardselector.feature.cardSelector.view.UrlImageWithLoader
import com.nattb8.godsunchainedcardselector.ui.theme.GodsUnchainedCardSelectorTheme
import com.nattb8.godsunchainedcardselector.ui.theme.White25
import com.nattb8.godsunchainedcardselector.ui.theme.White50

private const val HEADER_IMAGE_URL =
    "https://godsunchained.com/assets/images/backgrounds/teal-agrodor.jpg"
private const val HEADER_IMAGE_HEIGHT_DP = 300
private const val SELECTED_CARD_MIN_WIDTH_DP = 185
private const val SELECTED_CARD_MIN_HEIGHT_DP = 250
private const val NUM_COLUMNS = 5
private const val CARDS_LIST_CARD_MIN_HEIGHT_DP = 200

@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun CardsSelectorScreen(viewModel: CardSelectorViewModel = viewModel()) {
    val state = viewModel.state.collectAsState()

    CardSelectorContent(state.value, viewModel::process)
}

@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun CardSelectorContent(
    state: CardSelectorViewState,
    eventHandler: (CardSelectorViewEvent) -> Unit
) {
    var screenWidth by remember { mutableStateOf(0) }

    Column {
        LazyColumn(
            Modifier
                .imePadding()
                .fillMaxSize()
                .onGloballyPositioned { screenWidth = it.size.width }
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Header(state, eventHandler) }

            if (state.isLoading) item { CircularProgressIndicator() }
            else
            // Cards list
            // Create each Row instead of using LazyVerticalGrid as Compose does not allow nested
            // scroll view in the same direction. We need the whole screen to be scrollable so users
            // can still scroll to the header/search field on smaller screens
                items(state.cards.chunked(NUM_COLUMNS)) { row ->
                    CardsRow(row, screenWidth, eventHandler)
                }
        }

        if (state.isError) GenericErrorDialog { eventHandler(CardSelectorViewEvent.TryAgainTapped) }
    }

}

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@Composable
private fun Header(state: CardSelectorViewState, eventHandler: (CardSelectorViewEvent) -> Unit) {
    Column(
        Modifier.background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberImagePainter(HEADER_IMAGE_URL),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HEADER_IMAGE_HEIGHT_DP.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop
            )

            state.selectedCardImageUrl?.let {
                UrlImageWithLoader(
                    url = it,
                    contentDescription = state.selectedCardName,
                    modifier = Modifier.defaultMinSize(
                        minWidth = SELECTED_CARD_MIN_WIDTH_DP.dp,
                        minHeight = SELECTED_CARD_MIN_HEIGHT_DP.dp
                    )
                )
            }
        }

        // Only show search field once loading is done and there's no error
        if (!state.isLoading && !state.isError) {
            SearchField(eventHandler)
            Spacer(Modifier.size(32.dp))
        }
    }
}


@ExperimentalComposeUiApi
@Composable
private fun SearchField(eventHandler: (CardSelectorViewEvent) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = query,
        onValueChange = {
            query = it
            eventHandler(CardSelectorViewEvent.Searched(it))
        },
        placeholder = {
            Text(text = stringResource(id = R.string.search))
        },
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .border(1.dp, White25, RectangleShape),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            placeholderColor = White50
        ),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            // Close keyboard when user taps on Done button
            onDone = { keyboardController?.hide() }
        ),
    )
}

@ExperimentalCoilApi
@Composable
fun CardsRow(row: List<Card>, screenWidth: Int, eventHandler: (CardSelectorViewEvent) -> Unit) {
    val horizontalPadding = 16.dp
    // Calculate the width of each card from the screen width minus the paddings at the start and end
    val cardWidth =
        with(LocalDensity.current) { (screenWidth / NUM_COLUMNS).toDp() - horizontalPadding * 2 }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        row.forEach { card ->
            UrlImageWithLoader(
                url = card.plainImageUrl,
                contentDescription = card.name,
                modifier = Modifier
                    .width(cardWidth)
                    .defaultMinSize(minHeight = CARDS_LIST_CARD_MIN_HEIGHT_DP.dp)
                    .clickable { eventHandler(CardSelectorViewEvent.CardTapped(card)) })
        }
    }
}

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p, widthDp = 1024, heightDp = 720)
@Composable
fun CardSelectorContentPreview() {
    GodsUnchainedCardSelectorTheme {
        Surface(color = MaterialTheme.colors.background) {
            CardSelectorContent(CardSelectorViewState(isLoading = false, isError = false)) {}
        }
    }
}