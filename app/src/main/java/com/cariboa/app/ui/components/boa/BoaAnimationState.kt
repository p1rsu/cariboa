package com.cariboa.app.ui.components.boa

sealed class BoaAnimationState(
    val lottieAsset: String,
    val contentDescription: String,
) {
    data object Idle : BoaAnimationState("lottie/boa_idle.json", "Boa is waving hello")
    data object Thinking : BoaAnimationState("lottie/boa_thinking.json", "Boa is thinking and planning")
    data object Searching : BoaAnimationState("lottie/boa_searching.json", "Boa is searching")
    data object Excited : BoaAnimationState("lottie/boa_excited.json", "Boa found something exciting")
    data object Celebrating : BoaAnimationState("lottie/boa_celebrating.json", "Boa is celebrating")
    data object Confused : BoaAnimationState("lottie/boa_confused.json", "Boa is confused")
    data object Sleeping : BoaAnimationState("lottie/boa_sleeping.json", "Boa is sleeping")
    data object Traveling : BoaAnimationState("lottie/boa_traveling.json", "Boa is traveling")
}
