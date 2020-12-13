package representation.attacks

import representation.{Square, setBit}

class KingAttacks {
  val kingAttacks : Array[Long] =
    Array.tabulate(64)((Square(_)) andThen maskKingAttacks)

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
