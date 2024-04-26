package cinema

open class Seat(val row: Int, val column: Int)

class PricedSeat(row: Int, column: Int, val price: Int) : Seat(row, column)

class SoldSeat(val token: String, val ticket: PricedSeat?)