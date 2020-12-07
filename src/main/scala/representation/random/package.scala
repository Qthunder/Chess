package representation

package object random {
  var state = 1804289383
  def xorshift32() : Int = {
    var newState = state
    newState ^= newState << 13
    newState ^= newState >> 17
    newState ^= newState << 5
    state = newState
    newState
  }

  def random32Bits(): Long = xorshift32().toLong
  def random64Bits(): Long = {
    val a,b,c,d = random32Bits() & 0xFFFF
    a | b << 16 | c << 32 | d << 48
  }
  @inline def magicNumberCandidate(): Long = random64Bits() & random64Bits() & random64Bits()
}
