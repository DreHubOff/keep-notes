package com.jksol.keep.notes.ui.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jksol.keep.notes.MainScreenDemoData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    var listItems by mutableStateOf<List<MainScreenItem>>(emptyList())

    init {
        listItems = MainScreenDemoData.notesList()
    }
}