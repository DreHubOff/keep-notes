package com.jksol.keep.notes.ui.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    var listItems by mutableStateOf<List<MainScreenItem>>(emptyList())

    init {
        listItems = listOf(
            MainScreenItem.TextNote(
                "Welcome to Your Notes! ✨",
                "This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget."
            ),
        )
    }
}