package com.example.ainoc.util

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// This class is a special tool to remove the "splash" or "ripple" effect when you tap something.
// We use it on buttons or cards where we want a clean look without the default grey circle animation on click.
class NoRippleInteractionSource : MutableInteractionSource {
    // This tells the system that no visual interactions (like ripples) should happen.
    override val interactions: Flow<Interaction> = emptyFlow()

    // These functions are required by the system but do nothing, ensuring no effect is shown.
    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction): Boolean = true
}