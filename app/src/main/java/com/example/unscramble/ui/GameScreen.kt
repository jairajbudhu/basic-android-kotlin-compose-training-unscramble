/*
 * Copyright (c)2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.unscramble.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscramble.R
import com.example.unscramble.ui.theme.UnscrambleTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior
            )
        }
    ) { it ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
//                .padding(16.dp),
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameStatus(
                wordCount = gameUiState.currentWordCount,
                score = gameUiState.score
            )
            GameLayout(
                onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
                userGuess = gameViewModel.userGuess,
                onKeyboardDone = { gameViewModel.checkUserGuess() },
                currentScrambledWord = gameUiState.currentScrambledWord,
                isGuessWrong = gameUiState.isGuessedWordWrong
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    onClick = { gameViewModel.skipWord() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(stringResource(R.string.skip))
                }
                Button(
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 8.dp),
                    onClick = { gameViewModel.checkUserGuess() }
                ) {
                    Text(stringResource(R.string.submit))
                }
            }
            if (gameUiState.isGameOver) {
                FinalScoreDialog(
                    score = gameUiState.score,
                    onPlayAgain = { gameViewModel.resetGame() }
                )
            }
        }
    }
}

@Composable
private fun TopAppBar(
    scrollBehavior: TopAppBarScrollBehavior?,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row (modifier = modifier.background(MaterialTheme.colorScheme.background))
            {
                Image(
                    painter = painterResource(id = R.drawable.videogame),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(36.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
//        navigationIcon = navigationIconContent,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}
@Composable
fun GameStatus(wordCount: Int, score: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .size(48.dp),
    ) {
        Text(
            text = stringResource(R.string.word_count, wordCount),
            fontSize = 18.sp,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            text = stringResource(R.string.score, score),
            fontSize = 18.sp,
        )
    }
}

@Composable
fun GameLayout(
    currentScrambledWord: String,
    isGuessWrong: Boolean,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = currentScrambledWord,
            fontSize = 45.sp,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.instructions),
            fontSize = 17.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        OutlinedTextField(
            value = userGuess,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onUserGuessChanged,
            label = {
                if (isGuessWrong) {
                    Text(stringResource(R.string.wrong_guess))
                } else {
                    Text(stringResource(R.string.enter_your_word))
                }
            },
            isError = isGuessWrong,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onKeyboardDone() }
            ),
        )
    }
}

/*
 * Creates and shows an AlertDialog with final score.
 */
@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(stringResource(R.string.congratulations)) },
        text = { Text(stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    UnscrambleTheme {
        GameScreen()
    }
}