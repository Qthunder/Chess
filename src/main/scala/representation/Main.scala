package representation
import scala.util.chaining._
import representation.Square._
object Main extends App {

//  val bitBoard = 0L
//  printBitboard2(board =
//    bitBoard
//    .pipe(setBit(E4))
//    .pipe(setBit(H8))
//    .pipe(popBit(E4))
//    .pipe(setBit(H1))
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
  printBitboard(Attacks.maskPawnAttacks(Side.Black, F5))
  printBitboard(Attacks.maskPawnAttacks(Side.White, F4))
}
