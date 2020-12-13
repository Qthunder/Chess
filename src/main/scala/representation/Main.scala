package representation
import representation.Square._
import representation.attacks.SliderAttacks

import scala.util.chaining.scalaUtilChainingOps
object Main extends App {

//  val isValid = println {
//    Attacks.checkMagicNumber(
//      magicNumber = 0x40040844404084L,
//      occupancies = Array.tabulate(1 << Attacks.bishopOccupancyBitCounts(a8.index))(index =>
//        Attacks.setOccupancy(
//          index = index,
//          attackCount = Attacks.bishopOccupancyBitCounts(a8.index),
//          attacks = Attacks.maskBishopOccupancyBits(a8)
//        )
//      ),
//      relevantBits = Attacks.bishopOccupancyBitCounts(a8.index),
//      fullOccupancy = Attacks.maskBishopOccupancyBits(a8),
//      attacks = Array.tabulate(1 << Attacks.bishopOccupancyBitCounts(a8.index))(Attacks.bishopAttacks(a8, _))
//    )
//  }

  //  val bitBoard = 0L
  //  printBitboard2(board =
  //    bitBoard
  //    .pipe(setBit(E4))
  //    .pipe(setBit(H8))
  //    .pipe(popBit(E4))
  //    .pipe(setBit(a8))
  //    .pipe(popBit(E6))
  //  )

  //  var bitBoard = 0L
  //  for {
  //    x <- 0 until 8
  //    y <- 0 until 7
  //  } bitBoard = setBit(x * 8 + y)(bitBoard)
  //
  //  printBitboard(bitBoard)

  //  printBitboard(Attacks.maskPawnAttacks(E4, Side.White))
  //  printBitboard(Attacks.maskPawnAttacks(E4, Side.Black))
  //  printBitboard(Attacks.maskPawnAttacks(A7, Side.Black))
  //  val occupancy = -1
  //  printBitboard(occupancy)
  //  println(countBits(board = occupancy))
  //  println(Square(leastSignificantBitIndex(occupancy)))
  //  for (index <- 0 until 100) {
  //    printBitboard(Attacks.setOccupancy(index, countBits(Attacks.maskRookOccupancyBits(A1)), Attacks.maskRookOccupancyBits(A1)))
  //  }

//  println(random.xorshift32())
//  println(random.xorshift32())
//  println(random.xorshift32())
//  println(random.xorshift32())
//  println(random.xorshift32())
//  printBitboard((countBits(random.random64Bits())))
//  println(countBits(random.random64Bits()))
//  println(countBits(random.random64Bits()))
//  println(countBits(random.random64Bits()))
//
//  println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//
//  println(countBits(random.magicNumberCandidate()))
//  println(countBits(random.magicNumberCandidate()))
//  println(countBits(random.magicNumberCandidate()))
//  println(countBits(random.magicNumberCandidate()))
//  println(countBits(random.magicNumberCandidate()))


  val occupancy =
    0L
      .pipe(setBit(g6))
      .pipe(setBit(h7))
      .pipe(setBit(b7))
      .pipe(setBit(f2))
      .pipe(setBit(b4))

//  println(Attacks.bishopMagicNumbers.mkString("Array(\n", ",\n", ")"))

  printBitboard(SliderAttacks.Bishop.getAttacks(e4, occupancy))
}
