package representation.attacks

import representation.{Square, setBit}

class KnightAttacks {

  val knightAttacks : Array[Long] =
    Square.values.map(maskKnightAttacks).toArray

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

}
