package representation

import representation.Side.Side

final case class Board(
    var whiteKing: BitBoard,
    var whiteQueens: BitBoard,
    var whiteRooks: BitBoard,
    var whiteBishops: BitBoard,
    var whiteKnights: BitBoard,
    var whitePawns: BitBoard,

    var blackKing: BitBoard,
    var blackQueens: BitBoard,
    var blackRooks: BitBoard,
    var blackBishops: BitBoard,
    var blackKnights: BitBoard,
    var blackPawns: BitBoard,

    var castlingRights: CastlingRights,
    var side: Side,

    var allWhitePieces: BitBoard,
    var allBlackPieces: BitBoard,
    var allPieces: BitBoard
)
