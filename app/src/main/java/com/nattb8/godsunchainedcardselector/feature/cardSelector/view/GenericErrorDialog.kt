package com.nattb8.godsunchainedcardselector.feature.cardSelector.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nattb8.godsunchainedcardselector.R

@Composable
fun GenericErrorDialog(confirmButtonTapped: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(id = R.string.generic_error_title)) },
        text = { Text(stringResource(id = R.string.generic_error_message)) },
        confirmButton = {
            Text(
                stringResource(id = R.string.try_again),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { confirmButtonTapped() })
        })
}