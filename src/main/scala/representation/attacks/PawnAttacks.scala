package representation.attacks

import representation.Side.Side
import representation.{Side, Square, setBit}

object PawnAttacks {
  val pawnAttacks : Array[Array[Long]] =
    Array.tabulate(2, 64)((side, square) => maskPawnAttacks(Side(side), Square(square)))

  def maskPawnAttacks(side: Side, square: Square): Long = {
    val pieceBoard = setBit(square)(board = 0L)
    side match {
      case Side.White => ((pieceBoard & notAFile) >> 9) | ((pieceBoard & notHFile) >> 7)
      case Side.Black => ((pieceBoard & notAFile) << 7) | ((pieceBoard & notHFile) << 9)
    }
  }
}
