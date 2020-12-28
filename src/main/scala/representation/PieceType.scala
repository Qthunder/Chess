package representation

object PieceType extends Enumeration {
  type PieceType = Value
  val Pawn, Knight, Bishop, Rook, Queen, King : PieceType = Value
  val valueStream: LazyList[PieceType] = super.values.to(LazyList)
}
