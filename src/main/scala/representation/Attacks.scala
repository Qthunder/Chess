package representation

import representation.Side.Side
import representation.Square.Square

object Attacks {
  /**
   * 8  0 1 1 1 1 1 1 1
   * 7  0 1 1 1 1 1 1 1
   * 6  0 1 1 1 1 1 1 1
   * 5  0 1 1 1 1 1 1 1
   * 4  0 1 1 1 1 1 1 1
   * 3  0 1 1 1 1 1 1 1
   * 2  0 1 1 1 1 1 1 1
   * 1  0 1 1 1 1 1 1 1
   *    A B C D E F G H
   *
   * Bitboard:             -72340172838076674
   * Bitboard (UNSIGNED) : 18374403900871474942
   */
  val notAFile = -72340172838076674L

  /**
   * 8  0 0 1 1 1 1 1 1
   * 7  0 0 1 1 1 1 1 1
   * 6  0 0 1 1 1 1 1 1
   * 5  0 0 1 1 1 1 1 1
   * 4  0 0 1 1 1 1 1 1
   * 3  0 0 1 1 1 1 1 1
   * 2  0 0 1 1 1 1 1 1
   * 1  0 0 1 1 1 1 1 1
   * A B C D E F G H
   *
   * Bitboard:             -217020518514230020
   * Bitboard (UNSIGNED) : 18229723555195321596
   */
  val notABFile = -217020518514230020L
  /**
   * 8  1 1 1 1 1 1 1 0
   * 7  1 1 1 1 1 1 1 0
   * 6  1 1 1 1 1 1 1 0
   * 5  1 1 1 1 1 1 1 0
   * 4  1 1 1 1 1 1 1 0
   * 3  1 1 1 1 1 1 1 0
   * 2  1 1 1 1 1 1 1 0
   * 1  1 1 1 1 1 1 1 0
   *    A B C D E F G H
   *
   * Bitboard:             9187201950435737471
   * Bitboard (UNSIGNED) : 9187201950435737471
   */
  val notHFile = 9187201950435737471L

  /**
   * 8  1 1 1 1 1 1 0 0
   * 7  1 1 1 1 1 1 0 0
   * 6  1 1 1 1 1 1 0 0
   * 5  1 1 1 1 1 1 0 0
   * 4  1 1 1 1 1 1 0 0
   * 3  1 1 1 1 1 1 0 0
   * 2  1 1 1 1 1 1 0 0
   * 1  1 1 1 1 1 1 0 0
   * A B C D E F G H
   *
   * Bitboard:             4557430888798830399
   * Bitboard (UNSIGNED) : 4557430888798830399
   */
  val notHGFile = 4557430888798830399L

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
