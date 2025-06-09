@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.shared

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.contentSize
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

const val defaultTransitionAnimationDuration = 400

val LocalSharedTransitionSettings = compositionLocalOf<SharedTransitionSettings?> { null }

@Composable
fun rememberChecklistTransitionKey(checklistId: Long, component: String): Any =
    remember("chl$checklistId") { "chl${component}$checklistId" }

@Composable
fun rememberNextNoteTransitionKey(noteId: Long, component: String): Any =
    remember("tn$noteId") { "tn${component}$noteId" }

@Composable
fun rememberChecklistToEditorTransitionKey(checklistId: Long): Any =
    rememberChecklistTransitionKey(checklistId = checklistId, component = "card")

@Composable
fun rememberChecklistToEditorTitleTransitionKey(checklistId: Long): Any =
    rememberChecklistTransitionKey(checklistId = checklistId, component = "title")

@Composable
fun rememberTextNoteToEditorTransitionKey(noteId: Long): Any =
    rememberNextNoteTransitionKey(noteId = noteId, component = "card")

@Composable
fun rememberTextNoteToEditorTitleTransitionKey(noteId: Long): Any =
    rememberNextNoteTransitionKey(noteId = noteId, component = "title")

@Composable
fun Modifier.mainItemCardTransition(transitionKey: Any): Modifier {
    val fadeInEnterAnimDuration = remember { (defaultTransitionAnimationDuration * 0.3f).toInt() }
    val fadeOutExitAnimDuration = remember { (defaultTransitionAnimationDuration * 0.3f).toInt() }
    val fadeOutExitAnimDelay = remember { (defaultTransitionAnimationDuration * 0.7f).toInt() }
    return this.then(
        other = Modifier.sharedBoundsTransition(
            transitionKey = transitionKey,
            enter = expandVertically(
                animationSpec = tween(durationMillis = defaultTransitionAnimationDuration),
                expandFrom = Alignment.Top,
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = fadeInEnterAnimDuration,
                )
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = defaultTransitionAnimationDuration),
                shrinkTowards = Alignment.Top,
            ) + fadeOut(
                animationSpec = tween(
                    delayMillis = fadeOutExitAnimDuration,
                    durationMillis = fadeOutExitAnimDelay,
                )
            ),
        )
    )
}

@Composable
fun Modifier.sharedBoundsTransition(
    transitionKey: Any,
    boundsTransform: BoundsTransform = BoundsTransform { _, _ ->
        tween(
            durationMillis = defaultTransitionAnimationDuration,
            easing = FastOutSlowInEasing
        )
    },
    enter: EnterTransition = fadeIn(animationSpec = tween(durationMillis = defaultTransitionAnimationDuration)),
    exit: ExitTransition = fadeOut(animationSpec = tween(durationMillis = defaultTransitionAnimationDuration)),
    resizeMode: ResizeMode = ResizeMode.RemeasureToBounds,
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier {
    val settings = LocalSharedTransitionSettings.current ?: return this
    return with(settings.transitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(key = transitionKey),
            animatedVisibilityScope = settings.animationScope,
            boundsTransform = boundsTransform,
            enter = enter,
            exit = exit,
            resizeMode = resizeMode,
            placeHolderSize = placeHolderSize,
            renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            zIndexInOverlay = zIndexInOverlay,
        )
    }
}

@Composable
fun Modifier.sharedElementTransition(
    transitionKey: Any,
    boundsTransform: BoundsTransform = BoundsTransform { _, _ ->
        tween(durationMillis = defaultTransitionAnimationDuration, easing = FastOutSlowInEasing)
    },
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier {
    val settings = LocalSharedTransitionSettings.current ?: return this
    return with(settings.transitionScope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key = transitionKey),
            animatedVisibilityScope = settings.animationScope,
            boundsTransform = boundsTransform,
            placeHolderSize = placeHolderSize,
            renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            zIndexInOverlay = zIndexInOverlay,
        )
    }
}