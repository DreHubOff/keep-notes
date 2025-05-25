@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.shared

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope

class SharedTransitionSettings(
    val transitionScope: SharedTransitionScope,
    val animationScope: AnimatedContentScope,
)