package zmuzik.slidingpuzzle2.screens.game

enum class GameState {
    LOADING, // before pic is loaded and board initialized
    LOADED, // pic loaded, doing some animations
    READY_TO_SHUFFLE, // pic loaded and board (numbers) visible but not shuffled yet
    SHUFFLING, // shuffling in progress (animating), don't accept any touch events
    SHUFFLED, // ready to play
    PLAYING, // game in progress
    FINISHED  // game completed, don't accept touch events
}