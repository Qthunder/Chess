package representation.attacks

import representation.Side.Side
import representation.Square.Square
import representation.{Side, Square, setBit}

object Attacks {

  val pawnAttacks : Array[Array[Long]] =
    Array.tabulate(2, 64)((side, square) => maskPawnAttacks(Side(side), Square(square)))

  val knightAttacks : Array[Long] =
    Array.tabulate(64)((Square(_)) andThen maskKnightAttacks)

  val kingAttacks : Array[Long] =
    Array.tabulate(64)((Square(_)) andThen maskKingAttacks)

  def maskPawnAttacks(side: Side, square: Square): Long = {
    val pieceBoard = setBit(square)(board = 0L)
    side match {
      case Side.White => ((pieceBoard & notAFile) >> 9) | ((pieceBoard & notHFile) >> 7)
      case Side.Black => ((pieceBoard & notAFile) << 7) | ((pieceBoard & notHFile) << 9)
    }
  }

  def maskKnightAttacks(square: Square) : Long = {
    val pieceBoard = setBit(square)(board = 0L)
    ((pieceBoard & notAFile)  >> 17) |
    ((pieceBoard & notHFile)  >> 15) |
    ((pieceBoard & notABFile) >> 10) |
    ((pieceBoard & notHGFile) >> 6)  |
    ((pieceBoard & notHFile)  << 17) |
    ((pieceBoard & notAFile)  << 15) |
    ((pieceBoard & notHGFile) << 10) |
    ((pieceBoard & notABFile) << 6)
  }

  def maskKingAttacks(square: Square) : Long = {
    val pieceBoard = setBit(square)(board = 0L)
    ((pieceBoard & notAFile)  >> 1) |
    ((pieceBoard & notHFile)  >> 7) |
    (pieceBoard               >> 8) |
    ((pieceBoard & notAFile)  >> 9) |
    ((pieceBoard & notHFile)  << 1) |
    ((pieceBoard & notAFile)  << 7) |
    (pieceBoard               << 8) |
    ((pieceBoard & notHFile)  << 9)
  }

}
