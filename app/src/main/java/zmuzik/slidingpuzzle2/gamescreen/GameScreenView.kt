package zmuzik.slidingpuzzle2.gamescreen

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */

interface GameScreenView {

    fun finishWithMessage(stringId: Int)

    fun loadPicture(pictureUri: String)

    fun getMaxScreenDim(): Int

    fun hideShuffleIcon()
}
