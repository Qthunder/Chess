import representation.Square.Square
package object representation {

  object Square extends Enumeration {
    type Square = Value
    val
      A8, B8, C8, D8, E8, F8, G8, H8,
      A7, B7, C7, D7, E7, F7, G7, H7,
      A6, B6, C6, D6, E6, F6, G6, H6,
      A5, B5, C5, D5, E5, F5, G5, H5,
      A4, B4, C4, D4, E4, F4, G4, H4,
      A3, B3, C3, D3, E3, F3, G3, H3,
      A2, B2, C2, D2, E2, F2, G2, H2,
      A1, B1, C1, D1, E1, F1, G1, H1
    = Value
  }

  implicit class SquareOps(value: Square)  {
    def asBoard: Long = 1L <<value.id

  }

  @inline def getBit(board: Long, square: Int): Long = board & (1L << square)
  @inline def getBit(board: Long, square: Square): Long = board & (1L << square.id)

  @inline def setBit(square: Int)(board: Long): Long = board | (1L << square)
  @inline def setBit(square: Square.Value)(board: Long): Long = board | (1L << square.id)

  @inline def popBit(square: Int)(board: Long): Long = board & ~(1L << square)
  @inline def popBit(square: Square)(board: Long): Long = board & ~(1L << square.id)

  def printBitboard(board: Long) : Unit = println {
    val boardString =
      (0 until 8).map { rank =>
        val row =
          (0 until 8)
            .map(file => if (getBit(board, rank * 8 + file) == 0) 0 else 1)
            .mkString(" ")
        s"${8 - rank}  $row"
      }.mkString("\n")
    val unsignedBitboard = if ((board & Long.MinValue) == 0L) BigInt(board) else ((BigInt(Long.MaxValue) + 1) << 1) + BigInt(board)
    s"""
       |$boardString
       |   A B C D E F G H
       |
       |Bitboard:             $board
       |Bitboard (UNSIGNED) : $unsignedBitboard
       |""".stripMargin
  }
  def rankAndFile(square: Square): (Int, Int) = (square.id / 8, square.id % 8)

  @inline
  def countBits(board: Long): Int = {
    var newBoard = board
    var count = 0
    while (newBoard != 0) {
      newBoard &= newBoard - 1
      count += 1
    }
    count
  }

  @inline
  def leastSignificantBitIndex(board: Long): Int =
    board match {
      case 0 => -1
      case _ => countBits((board & -board) - 1)
    }
}
