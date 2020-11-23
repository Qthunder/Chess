package representation
import scala.util.chaining._
import representation.Square._
object Main extends App {

  val bitBoard = 0L
  printBitboard2(board =
    bitBoard
    .pipe(setBit(E4))
    .pipe(setBit(H8))
    .pipe(popBit(E4))
    .pipe(setBit(H1))
    .pipe(popBit(E6))
  )

}
