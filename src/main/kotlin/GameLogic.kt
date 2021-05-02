typealias GameLogic = (Piece) -> List<String>

class DefaultGameLogic(val rowNum: Int, val colNum: Int) : GameLogic {

    private val cells = (1..rowNum)
        .map { (it + 64).toChar() }
        .flatMap { (1..colNum).map { row -> "$it$row" } }

    override fun invoke(piece: Piece): List<String> = piece.possibleMoves()

    private fun Piece.possibleMoves(): List<String> {
        val curr = cells.indexOf(position)
        return (1..moves.steps())
            .flatMap { increment ->
                val iterator = colNum * increment
                directions
                    .map { getCell(it.formula(curr, iterator, increment, moves.steps())) }
                    .filter { it != "" }
            }
    }

    private fun getCell(index: Int) = cells.getOrElse(index, ::defaultValue)

    private fun Moves.steps() = when (this) {
        is Restricted -> steps
        Unlimited     -> rowNum
    }

    private fun defaultValue(value: Int) = ""

}

private fun Direction.formula(curr: Int, iterator: Int, increment: Int, moves: Int) = when (this) {
    Direction.TL  -> curr + (iterator - increment)
    Direction.T   -> curr + iterator
    Direction.TR  -> curr + (iterator + increment)
    Direction.L   -> curr - (1 * increment)
    Direction.R   -> curr + (1 * increment)
    Direction.BL  -> curr - (iterator + increment)
    Direction.B   -> curr - iterator
    Direction.BR  -> curr - (iterator - increment)
    Direction.TLL -> (curr + (moves - increment) + iterator + 1)
    Direction.TLR -> (curr - (moves - increment) + iterator - 1)
    Direction.BLL -> (curr + (moves - increment) - iterator + 1)
    Direction.BLR -> (curr - (moves - increment) - iterator - 1)
}