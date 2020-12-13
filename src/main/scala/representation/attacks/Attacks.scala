//package representation.attacks
//
//import representation.{Square, setBit, _}
//
//import java.util
//import scala.util.control.Breaks.{break, breakable}
//
//object Attacks {
//
//  val bishopOccupancies: Array[Long] =  Square.values.map(maskBishopOccupancyBits).toArray
//  val rookOccupancies: Array[Long] =  Square.values.map(maskRookOccupancyBits).toArray
//
//  val rookOccupancyBitCounts : Array[Int] = rookOccupancies.map(countBits)
//  val bishopOccupancyBitCounts : Array[Int] =  bishopOccupancies.map(countBits)
//
//  val rookMagicNumbers: Array[Long] =
//    Square.values.toArray.zip(rookOccupancyBitCounts).map {
//      case (square, occupancyBitCount) =>
//       println(s"Calculating rook magic number for square ${square.index}")
//       findMagicNumber(occupancyBitCount, calculateRookAttacks(square, _), maskRookOccupancyBits(square))
//    }
//
//  val bishopMagicNumbers : Array[Long] =
//    Square.values.toArray.zip(bishopOccupancyBitCounts).map {
//      case (square, occupancyBitCount) =>
//        println(s"Calculating bishop magic number for square ${square.index}")
//        findMagicNumber(occupancyBitCount, calculateBishopAttacks(square, _), maskBishopOccupancyBits(square))
//    }
//
//  val bishopAttacks : Array[Array[Long]] =
//    Square.values.map { square =>
//      val occupancyBitCount = bishopOccupancyBitCounts(square.index)
//      val occupancyCountForSquare = 1 << occupancyBitCount
//      val attacksForSquare: Array[Long] = Array.ofDim[Long](occupancyCountForSquare)
//      LazyList.tabulate(occupancyCountForSquare)(setOccupancy(_, occupancyBitCount, maskBishopOccupancyBits(square)))
//        .foreach { occupancy =>
//          val magicIndex = ((occupancy * bishopMagicNumbers(square.index) >> (64 - occupancyBitCount)) & ((1 << occupancyBitCount) - 1)).toInt
//          attacksForSquare.update(magicIndex, calculateBishopAttacks(square, occupancy))
//        }
//      attacksForSquare
//    }.toArray
//
//  val rookAttacks : Array[Array[Long]] =
//    Square.values.map { square =>
//      val occupancyBitCount = rookOccupancyBitCounts(square.index)
//      val occupancyCountForSquare = 1 << occupancyBitCount
//      val attacksForSquare: Array[Long] = Array.ofDim[Long](occupancyCountForSquare)
//      LazyList.tabulate(occupancyCountForSquare)(setOccupancy(_, occupancyBitCount, maskRookOccupancyBits(square)))
//        .foreach { occupancy =>
//          val magicIndex = ((occupancy * rookMagicNumbers(square.index) >> (64 - occupancyBitCount)) & ((1 << occupancyBitCount) - 1)).toInt
//          attacksForSquare.update(magicIndex, calculateRookAttacks(square, occupancy))
//        }
//      attacksForSquare
//    }.toArray
//
//  @inline
//  def getBishopAttacks(square: Square, occupancy: Long): Long = {
//    val relevantBits = bishopOccupancyBitCounts(square.index)
//
//    @inline val magicIndex = (((occupancy & bishopOccupancies(square.index)) * bishopMagicNumbers(square.index)) >> (64 - relevantBits)) & ((1 << relevantBits) - 1)
//    bishopAttacks.apply(square.index)(magicIndex.toInt)
//  }
//
//  @inline
//  def getRookAttacks(square: Square, occupancy: Long): Long = {
//    val relevantBits = rookOccupancyBitCounts(square.index)
//
//    @inline val magicIndex = (((occupancy & rookOccupancies(square.index)) * rookMagicNumbers(square.index)) >> (64 - relevantBits)) & ((1 << relevantBits) - 1)
//    rookAttacks.apply(square.index)(magicIndex.toInt)
//  }
//
//  def maskBishopOccupancyBits(square: Square) : Long = {
//    val (pieceRank, pieceFile) = rankAndFile(square)
//
//    val squares =
//      for {
//        rank <- 1 until 7
//        file <- 1 until 7 if rank + file == pieceRank + pieceFile || rank - file == pieceRank - pieceFile
//        targetSquare = rank * 8 + file if square.index != targetSquare
//      } yield 1L << targetSquare
//
//    squares.reduce(_ | _)
//  }
//
//  def maskRookOccupancyBits(square: Square) : Long = {
//    val (pieceRank, pieceFile) = rankAndFile(square)
//    (
//      ((1 until 7) map (_ -> pieceFile)) ++
//      ((1 until 7) map (pieceRank -> _))
//    ).collect {
//      case (rank, file) if rank != pieceRank || file != pieceFile => 1L << (rank * 8 + file)
//    }
//    .reduce(_ | _)
//  }
//
//  private def slidingPieceAttacks(square: Square, occupancy: Long, directions: List[(Int, Int)]) = {
//    val (pieceRank, pieceFile) = rankAndFile(square)
//    directions.flatMap { case (rankDirection, fileDirection) =>
//      (
//        LazyList.iterate(pieceRank)(_ + rankDirection).takeWhile((0 until 8).contains) zip
//          LazyList.iterate(pieceFile)(_ + fileDirection).takeWhile((0 until 8).contains)
//        )
//        .map { case (rank, file) => rank * 8 + file }
//        .takeWhile(square => 0 <= square && square < 64)
//        .map(1L << _)
//        .takeWhile(square => (occupancy & square) == 0)
//        .toList
//    }.fold(0L)(_ | _) ^ square.asBoard
//  }
//
//  def calculateBishopAttacks(square: Square, occupancy: Long) : Long = slidingPieceAttacks(square, occupancy, List((-1,1),(1,-1),(-1,-1),(1,1)))
//
//  def calculateRookAttacks(square: Square, occupancy: Long) : Long = slidingPieceAttacks(square, occupancy, List((1, 0), (-1, 0), (0, 1), (0, -1)))
//
//  @inline
//  def setOccupancy(index: Int, attackCount: Int, attacks: Long): Long = {
//    //TODO rewrite scala-y
//    var occupancy = 0L
//    var remainingAttacks = attacks
//    for (count <- 0 until attackCount) {
//      val square = leastSignificantBitIndex(remainingAttacks)
//      remainingAttacks = popBit(square)(remainingAttacks)
//      if ((index & (1 << count)) != 0)
//        occupancy |= 1L << square.index
//    }
//    occupancy
//  }
//
//  private def findMagicNumber(relevantBits: Int, attack: Long => Long, fullOccupancy: Long): Long = {
//    val occupanciesCount = 1 << relevantBits
//    val occupancies: Array[Long] = Array.tabulate(occupanciesCount)(setOccupancy(_, relevantBits, fullOccupancy))
//    val attacks: Array[Long] = occupancies.map(attack)
//    val usedAttacks = Array.fill(occupanciesCount)(0L)
//    var isValid = false
//    var magicNumber: Long = 0L
//    var count = 0
//    while (!isValid) {
//      magicNumber = random.magicNumberCandidate()
//      count += 1
//      isValid = checkMagicNumber(magicNumber, occupancies, relevantBits, fullOccupancy, attacks, usedAttacks)
//    }
//    magicNumber
//  }
//
//  @inline def checkMagicNumber(
//    magicNumber: Long,
//    occupancies: Array[Long],
//    relevantBits: Int,
//    fullOccupancy: Long,
//    attacks: Array[Long],
//    usedAttacks: Array[Long]
//  ): Boolean = {
//      util.Arrays.fill(usedAttacks, 0L)
//      (countBits((fullOccupancy * magicNumber) & 0xFF00000000000000L) >= 6) && {
//        var isValid = true
//        breakable {
//          for (index <- occupancies.indices) {
//              val occupancy = occupancies(index)
//              val magicIndex = ((occupancy * magicNumber >> (64 - relevantBits)) & ((1 << relevantBits) - 1)).toInt
//              if (usedAttacks(magicIndex.toInt) == 0L)
//                usedAttacks.update(magicIndex.toInt, attacks(index))
//              else if (usedAttacks(magicIndex.toInt) != attacks(index)) {
//                isValid = false
//                break()
//              }
//            }
//          }
//        isValid
//      }
//  }
//}
//
