package representation.attacks

import representation.Side.Side
import representation.{Side, Square, setBit}
import representation._

object Attacks {

  val pawnAttacks : Array[Array[Long]] =
    Array.tabulate(2, 64)((side, square) => maskPawnAttacks(Side(side), Square(square)))

  val knightAttacks : Array[Long] =
    Array.tabulate(64)((Square(_)) andThen maskKnightAttacks)

  val kingAttacks : Array[Long] =
    Array.tabulate(64)((Square(_)) andThen maskKingAttacks)

  val rookOccupancyBitCounts : Array[Int] = Array.tabulate(64)((Square(_)) andThen maskRookOccupancyBits andThen countBits)
  val bishopOccupancyBitCounts : Array[Int] = Array.tabulate(64)((Square(_)) andThen maskBishopOccupancyBits andThen countBits)

  val rookMagicNumbers: Array[Long] =
    Square.values.toArray.zip(rookOccupancyBitCounts).map {
      case (square, occupancyBitCount) =>
       println(s"Calculating rook magic number for piece ${square.index}")
       findMagicNumber(occupancyBitCount, rookAttacks(square, _), maskRookOccupancyBits(square)).get
    }

  val bishopMagicNumbers : Array[Long] =
    Square.values.toArray.zip(bishopOccupancyBitCounts).map {
      case (square, occupancyBitCount) =>
        println(s"Calculating bishop magic number for square ${square.index}")
        findMagicNumber(occupancyBitCount, bishopAttacks(square, _), maskBishopOccupancyBits(square)).get
    }
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
        targetSquare = rank * 8 + file if square.index != targetSquare
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
    }.fold(0L)(_ | _) ^ square.asBoard
  }

  def bishopAttacks(square: Square, occupancy: Long) : Long = slidingPieceAttacks(square, occupancy, List((-1,1),(1,-1),(-1,-1),(1,1)))

  def rookAttacks(square: Square, occupancy: Long) : Long = slidingPieceAttacks(square, occupancy, List((1, 0), (-1, 0), (0, 1), (0, -1)))

  def setOccupancy(index: Int, attackCount: Int, attacks: Long): Long = {
    //TODO rewrite scala-y
    var occupancy = 0L
    var remainingAttacks = attacks
    for (count <- 0 until attackCount) {
      val square = leastSignificantBitIndex(remainingAttacks)
      remainingAttacks = popBit(square)(remainingAttacks)
      if ((index & (1 << count)) != 0)
        occupancy |= 1L << square.index
    }
    occupancy
  }

  private def findMagicNumber(relevantBits: Int, attack: Long => Long, fullOccupancy: Long) = {
    val occupanciesCount = 1 << relevantBits
    val occupancies: Array[Long] = Array.tabulate(occupanciesCount)(setOccupancy(_, relevantBits, fullOccupancy))
    val attacks: Array[Long] = occupancies.map(attack)
    LazyList.continually(random.magicNumberCandidate()).find(checkMagicNumber(_, occupancies, relevantBits, fullOccupancy, attacks))
  }


  def checkMagicNumber(
    magicNumber: Long,
    occupancies: Array[Long],
    relevantBits: Int,
    fullOccupancy: Long,
    attacks: Array[Long]): Boolean = {
      val usedAttacks: Array[Long] = Array.fill(1 << relevantBits)(0L)
//          println(s"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//          println(s"Trying magic number:")
//          printBitboard(magicNumber)
      (countBits((fullOccupancy * magicNumber) & 0xFF00000000000000L) >= 6) &&
        occupancies.zipWithIndex.forall { case (occupancy, index) =>
//                    println(s"Occupancy #$index")
//                    printBitboard(occupancy)
          val mult = (occupancy * magicNumber)
//                    println("Multiplying occupancy by magic number")
//                    printBitboard(mult)
          val magicIndex = (mult >> (64 - relevantBits)) & ((1 << relevantBits) - 1)
//                    println(s"Checking magic index $magicIndex... ")
//                    printBitboard(magicIndex.toLong)
          (usedAttacks(magicIndex.toInt) == 0L && {
            usedAttacks.update(magicIndex.toInt, attacks(index));
            true
          }) // || usedAttacks(magicIndex.toInt) == attacks(index) || {println(s"Giving up at index $index"); false}
        }
  }
}

