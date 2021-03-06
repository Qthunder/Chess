import representation.CastlingRights.{BlackKingSide, BlackQueenSide, WhiteKingSide, WhiteQueenSide}
import representation.CastlingSide.CastlingSide
import representation.PieceType.PieceType
import representation.Side.Side

package object representation {
  type BitBoard = Long

//  trait IsIndex extends Any {
//    def index: Int
//  }
//
//  final class ArrayI[I <: IsIndex, T](val array: Array[T]) extends AnyVal {
//    @inline def apply(i: I): T = array(i.index)
//    @inline def update(i: I, value: T): Unit = array.update(i.index, value)
//  }

  class CastlingRights private(private val value: Int) extends AnyVal {
    @inline def +(other: CastlingRights) = new CastlingRights(value | other.value)

    @inline def canCastle(side: Side, castlingSide: CastlingSide): Boolean = (side, castlingSide) match {
      case (Side.White, CastlingSide.KingSide) => (value & WhiteKingSide.value) != 0
      case (Side.White, CastlingSide.QueenSide) => (value & WhiteQueenSide.value) != 0
      case (Side.Black, CastlingSide.KingSide) => (value & BlackKingSide.value) != 0
      case (Side.Black, CastlingSide.QueenSide) => (value & BlackQueenSide.value) != 0
    }

    @inline override def toString: String = {
      s"White: ${if (canCastle(Side.White, CastlingSide.QueenSide)) "⬅ " else "- "}${if (canCastle(Side.White, CastlingSide.KingSide)) "➡ " else "- " } Black: ${if (canCastle(Side.Black, CastlingSide.QueenSide)) "⬅ " else "- " }${if (canCastle(Side.Black, CastlingSide.QueenSide)) "➡ " else "- "}"
    }
  }
  object CastlingRights {
    val None = new CastlingRights(0)
    val WhiteQueenSide = new CastlingRights(1)
    val WhiteKingSide = new CastlingRights(2)
    val BlackKingSide = new CastlingRights(4)
    val BlackQueenSide = new CastlingRights(8)
    val WhiteBothSides: CastlingRights = WhiteKingSide + WhiteQueenSide
    val BlackBothSides: CastlingRights = BlackKingSide + BlackQueenSide
    val All: CastlingRights = WhiteBothSides + BlackBothSides
  }

  class Square private (val index: Int) extends AnyVal {
    @inline def asBoard: BitBoard = 1L << index
  }

  object Square {
    private val all: List[List[Square]] = List.tabulate(8, 8)(_ * 8 + _).map(_.map(Square(_)))
    val List(r8,r7,r6,r5,r4,r3,r2,r1) = all
    val List(a8, b8, c8, d8, e8, f8, g8, h8) = r8
    val List(a7, b7, c7, d7, e7, f7, g7, h7) = r7
    val List(a6, b6, c6, d6, e6, f6, g6, h6) = r6
    val List(a5, b5, c5, d5, e5, f5, g5, h5) = r5
    val List(a4, b4, c4, d4, e4, f4, g4, h4) = r4
    val List(a3, b3, c3, d3, e3, f3, g3, h3) = r3
    val List(a2, b2, c2, d2, e2, f2, g2, h2) = r2
    val List(a1, b1, c1, d1, e1, f1, g1, h1) = r1
    val values: List[Square] = all.flatten
    val NO_SQUARE = new Square(-1)
    @inline def apply(index: Int) = new Square(index)
  }



  @inline def getBit(board: BitBoard, square: Square): BitBoard = board & (1L << square.index)

  @inline def setBit(square: Square)(board: BitBoard): BitBoard = board | (1L << square.index)

  @inline def popBit(square: Square)(board: BitBoard): BitBoard = board & ~(1L << square.index)

  def printBitboard(board: BitBoard) : Unit = println {
    val boardString =
      (0 until 8).map { rank =>
        val row =
          (0 until 8)
            .map(file => if (getBit(board, Square(rank * 8 + file)) == 0) "⬜" else "⬛")
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
  def rankAndFile(square: Square): (Int, Int) = (square.index / 8, square.index % 8)

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
  def leastSignificantBitIndex(board: Long): Square =
    board match {
      case 0 => Square.NO_SQUARE
      case _ => Square(countBits((board & -board) - 1))
    }

  val sideAndPieceToChar: (Side, PieceType) => String = {
    case (Side.White, PieceType.Pawn) => "♙"
    case (Side.White, PieceType.Knight) => "♘"
    case (Side.White, PieceType.Bishop) => "♗"
    case (Side.White, PieceType.Rook) => "♗"
    case (Side.White, PieceType.Queen) => "♕"
    case (Side.White, PieceType.King) => "♔"
    case (Side.Black, PieceType.Pawn) => "♟"
    case (Side.Black, PieceType.Knight) => "♞"
    case (Side.Black, PieceType.Bishop) => "♝"
    case (Side.Black, PieceType.Rook) => "♜"
    case (Side.Black, PieceType.Queen) => "♛"
    case (Side.Black, PieceType.King) => "♚"
  }
}
