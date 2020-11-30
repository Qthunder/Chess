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
}
