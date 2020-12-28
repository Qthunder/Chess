package representation

import representation.PieceType.PieceType
import representation.Side.Side

final case class Board(
    var whiteKing: BitBoard,
    var whiteQueens: BitBoard,
    var whiteRooks: BitBoard,
    var whiteBishops: BitBoard,
    var whiteKnights: BitBoard,
    var whitePawns: BitBoard,

    var blackKing: BitBoard,
    var blackQueens: BitBoard,
    var blackRooks: BitBoard,
    var blackBishops: BitBoard,
    var blackKnights: BitBoard,
    var blackPawns: BitBoard,

    var castlingRights: CastlingRights,
    var side: Side,

    var allWhitePieces: BitBoard,
    var allBlackPieces: BitBoard,
    var allPieces: BitBoard
) {

  def bitBoard(side: Side, piece: PieceType): BitBoard = (side, piece) match {
      case (Side.White, PieceType.Pawn) => whitePawns
      case (Side.White, PieceType.Knight) => whiteKnights
      case (Side.White, PieceType.Bishop) => whiteBishops
      case (Side.White, PieceType.Rook) => whiteRooks
      case (Side.White, PieceType.Queen) => whiteQueens
      case (Side.White, PieceType.King) => whiteKing
      case (Side.Black, PieceType.Pawn) => blackPawns
      case (Side.Black, PieceType.Knight) => blackKnights
      case (Side.Black, PieceType.Bishop) =>blackBishops
      case (Side.Black, PieceType.Rook) => blackRooks
      case (Side.Black, PieceType.Queen) => blackQueens
      case (Side.Black, PieceType.King) => blackKing
  }

  def print(): Unit = println {
    toString
  }
  override def toString: String = {
    val board =
      (0 until 8).map { rank =>
      val row =
       (0 until 8).map { file =>
            val square = Square(rank * 8 + file)
            val boards =
              for {
                side <- Side.values.to(LazyList)
                piece <- PieceType.valueStream
              } yield (side, piece, bitBoard(side, piece))
            boards.collectFirst {
                case (side, piece, bitBoard) if (bitBoard & square.asBoard) != 0L => (side, piece)
            }.fold("⛝")(sideAndPieceToChar.tupled)
       }.mkString(" ")
        s"${8 - rank}  $row"
      }.mkString("\n")
    s"""
       |$board
       |   A B C D E F G H
       |   Castling Rights : $castlingRights
       |   To Move: $side
       |""".stripMargin
  }
}

object Board {
  def starting: Board =
      Board(
        Square.e1.asBoard,
        Square.d1.asBoard,
        Square.a1.asBoard | Square.h1.asBoard,
        Square.c1.asBoard | Square.f1.asBoard,
        Square.b1.asBoard | Square.g1.asBoard,
        Square.r2.map(_.asBoard).reduce(_ | _),
        Square.e8.asBoard,
        Square.d8.asBoard,
        Square.a8.asBoard | Square.h8.asBoard,
        Square.c8.asBoard | Square.f8.asBoard,
        Square.b8.asBoard | Square.g8.asBoard,
        Square.r7.map(_.asBoard).reduce(_ | _),
        CastlingRights.All,
        Side.White,
        0L,
        0L,
        0L
  )
}
