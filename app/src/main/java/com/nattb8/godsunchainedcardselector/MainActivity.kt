package com.nattb8.godsunchainedcardselector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import coil.annotation.ExperimentalCoilApi
import com.nattb8.godsunchainedcardselector.feature.cardSelector.CardsSelectorScreen
import com.nattb8.godsunchainedcardselector.ui.theme.GodsUnchainedCardSelectorTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GodsUnchainedCardSelectorTheme {
                Surface(color = MaterialTheme.colors.background) {
                    CardsSelectorScreen()
                }
            }
        }
    }
}