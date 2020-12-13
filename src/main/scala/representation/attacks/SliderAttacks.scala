package representation.attacks

import representation.{BitBoard, Square, countBits, leastSignificantBitIndex, popBit, random, rankAndFile}

import scala.util.control.Breaks.{break, breakable}

abstract class SliderAttacks(directions: List[(Int, Int)]) {

  @inline
  def getAttacks(square: Square, occupancy: Long): Long = {
    val relevantBits = occupancyBitCounts(square.index)

    @inline val magicIndex = (((occupancy & occupancies(square.index)) * magicNumbers(square.index)) >> (64 - relevantBits)) & ((1 << relevantBits) - 1)
    attacks(square.index)(magicIndex.toInt)
  }


  protected def calculateOccupancyBits(square: Square): BitBoard

  private val occupancies: Array[BitBoard] =  Square.values.map(calculateOccupancyBits).toArray

  private val occupancyBitCounts : Array[Int] = occupancies.map(countBits)

  private val magicNumbers =
    Square.values.toArray.zip(occupancyBitCounts).map {
      case (square, occupancyBitCount) =>
        println(s"Calculating ${this.getClass.getSimpleName} magic number for square ${square.index}")
        findMagicNumber(occupancyBitCount, calculateAttacks(square, _), calculateOccupancyBits(square))
    }

  val attacks : Array[Array[BitBoard]] =
    Square.values.map { square =>
      val occupancyBitCount = occupancyBitCounts(square.index)
      val occupancyCountForSquare = 1 << occupancyBitCount
      val attacksForSquare: Array[Long] = Array.ofDim[Long](occupancyCountForSquare)
      LazyList.tabulate(occupancyCountForSquare)(setOccupancy(_, occupancyBitCount, calculateOccupancyBits(square)))
        .foreach { occupancy =>
          val magicIndex = ((occupancy * magicNumbers(square.index) >> (64 - occupancyBitCount)) & ((1 << occupancyBitCount) - 1)).toInt
          attacksForSquare.update(magicIndex, calculateAttacks(square, occupancy))
        }
      attacksForSquare
    }.toArray

  private def calculateAttacks(square: Square, occupancy: BitBoard) = {
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

  @inline
  private def setOccupancy(index: Int, attackCount: Int, attacks: Long): Long = {
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

  private def findMagicNumber(relevantBits: Int, attack: Long => Long, fullOccupancy: Long): Long = {
    val occupanciesCount = 1 << relevantBits
    val occupancies: Array[Long] = Array.tabulate(occupanciesCount)(setOccupancy(_, relevantBits, fullOccupancy))
    val attacks: Array[Long] = occupancies.map(attack)
    val usedAttacks = Array.fill(occupanciesCount)(0L)
    var isValid = false
    var magicNumber: Long = 0L
    var count = 0
    while (!isValid) {
      magicNumber = random.magicNumberCandidate()
      count += 1
      isValid = checkMagicNumber(magicNumber, occupancies, relevantBits, fullOccupancy, attacks, usedAttacks)
    }
    magicNumber
  }

  @inline def checkMagicNumber(
    magicNumber: Long,
    occupancies: Array[Long],
    relevantBits: Int,
    fullOccupancy: Long,
    attacks: Array[Long],
    usedAttacks: Array[Long]
  ): Boolean = {
    java.util.Arrays.fill(usedAttacks, 0L)
    (countBits((fullOccupancy * magicNumber) & 0xFF00000000000000L) >= 6) && {
      var isValid = true
      breakable {
        for (index <- occupancies.indices) {
          val occupancy = occupancies(index)
          val magicIndex = ((occupancy * magicNumber >> (64 - relevantBits)) & ((1 << relevantBits) - 1)).toInt
          if (usedAttacks(magicIndex.toInt) == 0L)
            usedAttacks.update(magicIndex.toInt, attacks(index))
          else if (usedAttacks(magicIndex.toInt) != attacks(index)) {
            isValid = false
            break()
          }
        }
      }
      isValid
    }
  }

}

object SliderAttacks {

  object Rook extends SliderAttacks(List((1, 0), (-1, 0), (0, 1), (0, -1))) {
    override protected def calculateOccupancyBits(square: Square): BitBoard = {
      val (pieceRank, pieceFile) = rankAndFile(square)
      (
        ((1 until 7) map (_ -> pieceFile)) ++
          ((1 until 7) map (pieceRank -> _))
        ).collect {
        case (rank, file) if rank != pieceRank || file != pieceFile => 1L << (rank * 8 + file)
      }
        .reduce(_ | _)
    }
  }

  object Bishop extends SliderAttacks(List((-1, 1), (1, -1), (-1, -1), (1, 1))) {
    override protected def calculateOccupancyBits(square: Square): BitBoard = {
      val (pieceRank, pieceFile) = rankAndFile(square)

      val squares =
        for {
          rank <- 1 until 7
          file <- 1 until 7 if rank + file == pieceRank + pieceFile || rank - file == pieceRank - pieceFile
          targetSquare = rank * 8 + file if square.index != targetSquare
        } yield 1L << targetSquare

      squares.reduce(_ | _)
    }
  }

}
