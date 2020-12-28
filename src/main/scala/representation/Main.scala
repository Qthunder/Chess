package representation
import representation.Square._
import representation.attacks.SliderAttacks

import scala.util.chaining.scalaUtilChainingOps
object Main extends App {

  val occupancy =
    0L
      .pipe(setBit(g6))
      .pipe(setBit(h7))
      .pipe(setBit(b7))
      .pipe(setBit(f2))
      .pipe(setBit(b4))

  printBitboard(SliderAttacks.Bishop.getAttacks(e4, occupancy))

  Board.starting.print()
}
