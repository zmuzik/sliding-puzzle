package zmuzik.slidingpuzzle2.screens.game


enum class GameState {
    /**
     * Before pic is loaded and board initialized
     */
    LOADING,

    /**
     * Pic loaded, doing some animations
     */
    LOADED,

    /**
     * Pic loaded and board (numbers) visible but not shuffled yet
     */
    READY_TO_SHUFFLE,

    /**
     * Shuffling in progress (animating), don't accept any touch events
     */
    SHUFFLING,

    /**
     * Ready to play
     */
    SHUFFLED,

    /**
     * Game in progress
     */
    PLAYING,

    /**
     * Game completed, don't accept touch events
     */
    FINISHED
}
