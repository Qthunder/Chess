package representation.fen

import cats.Functor
import representation.{Piece, PieceType, Side, fen}
import representation.PieceType.PieceType

import scala.util.parsing.combinator._
import cats.syntax.all._



object Parser extends RegexParsers {
  final case class Rank(squares: List[Either[Int, Piece]])
  object Rank {
    def apply(rank : Option[Piece] ~ List[Int ~ List[Piece]] ~ Option[Int]) = rank match {
      case maybePiece ~ squares ~ maybeEmpty => rank
    }
  }
  implicit val parserFunctor: Functor[Parser] = new Functor[Parser] { override def map[A, B](fa: Parser[A])(f: A => B): Parser[B] = fa.map(f)  }
  implicit val parserRing = cats.Semigroup
  import PieceType._
  import Side._
  def `1_to_7`: Parser[Int] =  "[1-7]".r ^^ (_.toInt)
  def `1_to_8`: Parser[Int] =  "[1-8]".r ^^ (_.toInt)
  def `8`: fen.Parser.Parser[Int] = elem('8').map(_.toInt)
  def piece : Parser[Piece] = {
      List(
        'p' -> Piece(Pawn, Black),
        'n' -> Piece(Knight, Black),
        'b' -> Piece(Bishop, Black),
        'r' -> Piece(Rook, Black),
        'q' -> Piece(Queen, Black),
        'k' -> Piece(King, Black),
        'P' -> Piece(Pawn, White),
        'N' -> Piece(Knight, White),
        'B' -> Piece(Bishop, White),
        'R' -> Piece(Rook, White),
        'Q' -> Piece(Queen, White),
        'K' -> Piece(King, White)
      ).map { case (char, piece) => elem(char).as(piece) }.reduce(_ | _)
  }

  def rank: Parser[Option[Piece] ~ List[Int ~ List[Piece]] ~ Option[Int]] = (`piece`.? ~  (`1_to_8` ~ piece.+).* ~ `1_to_8`.?).map(Rank(_))
  def rankWithPiece: Parser[List[]] = (piece ~ rankWithPiece) | rankWithEmpty
  def rankWithEmpty =

}

