package com.jksol.keep.notes.ui.animation

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration

fun defaultAnimationSpec(): TweenSpec<Float> = tween(durationMillis = defaultTransitionAnimationDuration)

fun scaleInFromBottomRight(
    durationMillis: Int = defaultTransitionAnimationDuration,
) = scaleIn(animationSpec = defaultAnimationSpec(), transformOrigin = TransformOrigin(1f, 1f))

fun scaleOutToBottomRight(
    durationMillis: Int = defaultTransitionAnimationDuration,
) = scaleOut(animationSpec = defaultAnimationSpec(), transformOrigin = TransformOrigin(1f, 1f))