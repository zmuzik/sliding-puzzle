package zmuzik.slidingpuzzle2.gamescreen

import android.content.Context
import android.graphics.Bitmap
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.PreferencesHelper
import zmuzik.slidingpuzzle2.common.Toaster
import java.util.*
import javax.inject.Inject

class PuzzleBoardView : ViewGroup {

    internal val TAG = this.javaClass.simpleName
    internal val SHUFFLE_STIFFNESS = SpringForce.STIFFNESS_LOW
    internal val SHUFFLE_DAMPING_RATIO = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY

    val displayNumbers: Boolean by lazy { prefsHelper.displayTileNumbers }

    internal var tilesX: Int = 0
    internal var tilesY: Int = 0
    lateinit var tiles: Array<Array<TileView>>

    var completePictureBitmap: Bitmap? = null

    private var tileWidth: Int = 0
    private var tileHeight: Int = 0

    private var downX: Int = 0
    private var downY: Int = 0
    private var moveDeltaX: Int = 0
    private var moveDeltaY: Int = 0
    private var activeTileX: Int = 0
    private var activeTileY: Int = 0
    private var blackTileX: Int = 0
    private var blackTileY: Int = 0
    private var state = State.LOADING

    @Inject
    lateinit var prefsHelper: PreferencesHelper
    @Inject
    lateinit var toaster: Toaster
    @Inject
    lateinit var view: GameScreenView
    @Inject
    lateinit var presenter: GameScreenPresenter

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    internal enum class State {
        LOADING, // before pic is loaded and board initialized
        LOADED, // board loaded but not shuffled yet
        SHUFFLING, // shuffling in progress (animating), don't accept any touch events
        SHUFFLED, // ready to play
        PLAYING, // game in progress
        FINISHED  // game completed, don't accept touch events
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (context is GameActivity) (context as GameActivity).component.inject(this)
    }

    fun setDimensions(width: Int, height: Int) {
        val params = layoutParams
        params.height = height
        params.width = width
        layoutParams = params
        invalidate()
        val shorterSideTiles = prefsHelper.gridDimLong
        val longerSideTiles = prefsHelper.gridDimShort
        tilesX = if (width < height) longerSideTiles else shorterSideTiles
        tilesY = if (width < height) shorterSideTiles else longerSideTiles

        tileWidth = width / tilesX
        tileHeight = height / tilesY
    }

    fun setBitmap(bitmap: Bitmap) {
        completePictureBitmap = bitmap
        initTiles()
    }

    inline fun <reified INNER> array2d(
            sizeOuter: Int, sizeInner: Int, noinline innerInit: (Int) -> INNER): Array<Array<INNER>>
            = Array(sizeOuter) { Array<INNER>(sizeInner, innerInit) }

    internal fun initTiles() {
        tiles = array2d<TileView>(tilesX, tilesY) { TileView(context) }
        var tileNumber = 1
        for (y in 0..tilesY - 1) {
            for (x in 0..tilesX - 1) {
                val tileBitmap = Bitmap.createBitmap(completePictureBitmap,
                        x * tileWidth, y * tileHeight,
                        tileWidth, tileHeight)
                tiles[x][y] = TileView(context, x, y, tileBitmap, tileNumber)
                tiles[x][y].setDisplayNumbers(displayNumbers)
                addView(tiles[x][y])
                tileNumber++
            }
        }
        // sets the black tile to the last tile of the grid
        blackTileX = tilesX - 1
        blackTileY = tilesY - 1
        state = State.LOADED
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        (0..childCount - 1)
                .map { getChildAt(it) }
                .forEach { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (state == State.LOADING) return
        for (i in 0..tilesX - 1) {
            for (j in 0..tilesY - 1) {
                if (state == State.FINISHED || i != blackTileX || j != blackTileY) {
                    var x = i * tileWidth
                    var y = j * tileHeight
                    if (isHorizPlayable(i, j)) {
                        x += moveDeltaX
                    } else if (isVertPlayable(i, j)) {
                        y += moveDeltaY
                    }
                    tiles[i][j].layout(x, y, x + tileWidth, y + tileHeight)
                }
            }
        }
    }

    private fun isPuzzleComplete(): Boolean {
        for (i in 0..tilesX - 1) {
            for (j in 0..tilesY - 1) {
                if (tiles[i][j].origX != i || tiles[i][j].origY != j) {
                    return false
                }
            }
        }
        return true
    }

    fun isHorizPlayable(x: Int, y: Int): Boolean {
        return y == blackTileY && y == activeTileY &&
                (x in activeTileX..(blackTileX - 1) || x in (blackTileX + 1)..activeTileX)
    }

    fun isVertPlayable(x: Int, y: Int): Boolean {
        return x == blackTileX && x == activeTileX &&
                (y in activeTileY..(blackTileY - 1) || y in (blackTileY + 1)..activeTileY)
    }

    fun maybeShuffle() {
        if (state == State.LOADED || state == State.SHUFFLED) {
            shuffle()
        }
    }

    fun shuffle() {
        state = State.SHUFFLING
        view.hideShuffleIcon()
        requestLayout()
        val random = Random()
        var position: Int
        val steps = tilesX * tilesY * 4
        for (step in 0..steps - 1) {
            if (step % 2 == 1) {
                position = random.nextInt(tilesX - 1)
                if (position >= blackTileX) position++
                playTile(position, blackTileY, true)
            } else {
                position = random.nextInt(tilesY - 1)
                if (position >= blackTileY) position++
                playTile(blackTileX, position, true)
            }
        }

        var animX: SpringAnimation? = null
        var animY: SpringAnimation? = null
        for (x in 0..tilesX - 1) {
            for (y in 0..tilesY - 1) {
                if (x == blackTileX && y == blackTileY) continue
                val tile = tiles[x][y]
                val endX = x * tileWidth
                val endY = y * tileHeight
                animX = SpringAnimation(tile, SpringAnimation.X, endX.toFloat())
                animY = SpringAnimation(tile, SpringAnimation.Y, endY.toFloat())
                animX.spring
                        .setStiffness(SHUFFLE_STIFFNESS).dampingRatio = SHUFFLE_DAMPING_RATIO
                animY.spring
                        .setStiffness(SHUFFLE_STIFFNESS).dampingRatio = SHUFFLE_DAMPING_RATIO
                animX.start()
                animY.start()
            }
        }
        if (animY == null) {
            state = State.SHUFFLED
            requestLayout()
        } else {
            animY.addEndListener { animation, canceled, value, velocity ->
                state = State.SHUFFLED
                requestLayout()
            }
        }
    }

    fun playTile(x: Int, y: Int, isShuffleMove: Boolean) {
        if (!isShuffleMove) state = State.PLAYING
        val temp = tiles[blackTileX][blackTileY]
        if (x == blackTileX) {
            if (y < blackTileY) {
                for (i in blackTileY - 1 downTo y) {
                    tiles[blackTileX][i + 1] = tiles[blackTileX][i]
                }
            } else if (y > blackTileY) {
                for (i in blackTileY + 1..y) {
                    tiles[blackTileX][i - 1] = tiles[blackTileX][i]
                }
            }
        } else if (y == blackTileY) {
            if (x < blackTileX) {
                for (i in blackTileX - 1 downTo x) {
                    tiles[i + 1][blackTileY] = tiles[i][blackTileY]
                }
            } else if (x > blackTileX) {
                for (i in blackTileX + 1..x) {
                    tiles[i - 1][blackTileY] = tiles[i][blackTileY]
                }
            }
        }
        tiles[x][y] = temp
        blackTileX = x
        blackTileY = y
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (state) {
            State.LOADING, State.SHUFFLING, State.FINISHED -> return true
            State.LOADED -> {
                shuffle()
                return true
            }
            else -> {
            }
        }

        val eventX = event.x.toInt()
        val eventY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (state == State.SHUFFLED || state == State.PLAYING) {
                    downX = eventX
                    downY = eventY
                    activeTileX = downX / tileWidth
                    activeTileY = downY / tileHeight
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (activeTileX == blackTileX) {
                    moveDeltaY = eventY - downY
                    moveDeltaX = 0
                    if (activeTileY < blackTileY) {
                        moveDeltaY = if (moveDeltaY > tileHeight) tileHeight else moveDeltaY
                        moveDeltaY = if (moveDeltaY < 0) 0 else moveDeltaY
                    } else if (activeTileY > blackTileY) {
                        moveDeltaY = if (moveDeltaY < -tileHeight) -tileHeight else moveDeltaY
                        moveDeltaY = if (moveDeltaY > 0) 0 else moveDeltaY
                    }
                } else if (activeTileY == blackTileY) {
                    moveDeltaX = eventX - downX
                    moveDeltaY = 0
                    if (activeTileX < blackTileX) {
                        moveDeltaX = if (moveDeltaX > tileWidth) tileWidth else moveDeltaX
                        moveDeltaX = if (moveDeltaX < 0) 0 else moveDeltaX
                    } else if (activeTileX > blackTileX) {
                        moveDeltaX = if (moveDeltaX < -tileWidth) -tileWidth else moveDeltaX
                        moveDeltaX = if (moveDeltaX > 0) 0 else moveDeltaX
                    }
                }
                requestLayout()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val tilesToMove = getTilesForAnimation(activeTileX, activeTileY, blackTileX, blackTileY)
                val makeTheMove = Math.abs(moveDeltaX) > tileWidth / 2 || Math.abs(moveDeltaY) > tileHeight / 2
                val moveX = activeTileY == blackTileY
                val moveY = activeTileX == blackTileX

                if (makeTheMove) {
                    playTile(activeTileX, activeTileY, false)
                    if (isPuzzleComplete()) {
                        onGameFinished()
                    }
                }
                requestLayout()
                moveDeltaX = 0
                moveDeltaY = 0

                for (tile in tilesToMove) {
                    var anim: SpringAnimation? = null
                    if (moveX) {
                        val endX = getTileX(tile) * tileWidth
                        anim = SpringAnimation(tile, SpringAnimation.X, endX.toFloat())
                    } else if (moveY) {
                        val endY = getTileY(tile) * tileHeight
                        anim = SpringAnimation(tile, SpringAnimation.Y, endY.toFloat())
                    }
                    anim?.spring?.setStiffness(SpringForce.STIFFNESS_HIGH)?.dampingRatio =
                            SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                    anim?.start()
                }
                return true
            }
        }
        return true
    }

    private fun onGameFinished() {
        state = State.FINISHED
        toaster.show(R.string.congrats)
        for (y in 0..tilesY - 1) {
            for (x in 0..tilesX - 1) {
                tiles[x][y].setDisplayNumbers(false)
                tiles[x][y].invalidate()
            }
        }
    }

    internal fun getTileX(tile: TileView): Int {
        for (y in 0..tilesY - 1) {
            for (x in 0..tilesX - 1) {
                if (tiles[x][y] == tile) return x
            }
        }
        return -1
    }

    internal fun getTileY(tile: TileView): Int {
        for (y in 0..tilesY - 1) {
            for (x in 0..tilesX - 1) {
                if (tiles[x][y] == tile) return y
            }
        }
        return -1
    }

    internal fun getTilesForAnimation(activeX: Int, activeY: Int, blackX: Int, blackY: Int): List<TileView> {
        val result = ArrayList<TileView>()
        if (activeX == blackX) {
            if (activeY < blackY) {
                (activeY..blackY - 1).mapTo(result) { tiles[activeX][it] }
            } else if (blackY < activeY) {
                (blackY + 1..activeY).mapTo(result) { tiles[activeX][it] }
            }
        } else if (activeY == blackY) {
            if (activeX < blackX) {
                (activeX..blackX - 1).mapTo(result) { tiles[it][activeY] }
            } else if (blackX < activeX) {
                (blackX + 1..activeX).mapTo(result) { tiles[it][activeY] }
            }
        }
        return result
    }
}
