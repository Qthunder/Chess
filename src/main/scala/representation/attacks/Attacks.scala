package representation.attacks

import representation.Side.Side
import representation.Square.Square
import representation.{Side, Square, setBit}
import representation._

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

  def maskBishopOccupancyBits(square: Square) : Long = {
    val (pieceRank, pieceFile) = rankAndFile(square)

    val squares =
      for {
        rank <- 1 until 7
        file <- 1 until 7 if rank + file == pieceRank + pieceFile || rank - file == pieceRank - pieceFile
        targetSquare = rank * 8 + file if square.id != targetSquare
      } yield 1L << targetSquare

    squares.reduce(_ | _)
  }

  def maskRookOccupancyBits(square: Square) : Long = {
    val (pieceRank, pieceFile) = rankAndFile(square)
    (
      ((1 until 7) map (_ -> pieceFile)) ++
      ((1 until 7) map (pieceRank -> _))
    ).collect {
      case (rank, file) if rank != pieceRank || file != pieceFile => 1L << (rank * 8 + file)
    }
    .reduce(_ | _)
  }

  private def slidingPieceAttacks(square: Square, occupancy: Long, directions: List[(Int, Int)]) = {
    val (pieceRank, pieceFile) = rankAndFile(square)
    directions.flatMap { case (rankDirection, fileDirection) =>
      (
        LazyList.iterate(pieceRank)(_ + rankDirection).takeWhile((0 until 8).contains) zip
          LazyList.iterate(pieceFile)(_ + fileDirection).takeWhile((0 until 8).contains)
        )
        .map { case (rank, file) => rank * 8 + file }
        .takeWhile(square => 0 <= square && square < 64)
        .map(1L << _)
        .takeWhile(square => (occupancy & square) == 0)
        .toList
    }.fold(0L)(_ | _)  ^ square.asBoard
  }

  def bishopAttacks(square: Square, occupancy: Long) : Long = slidingPieceAttacks(square, occupancy, List((-1,1),(1,-1),(-1,-1),(1,1)))

  def rookAttacks(square: Square, occupancy: Long) : Long = slidingPieceAttacks(square, occupancy, List((1, 0), (-1, 0), (0, 1), (0, -1)))


}
