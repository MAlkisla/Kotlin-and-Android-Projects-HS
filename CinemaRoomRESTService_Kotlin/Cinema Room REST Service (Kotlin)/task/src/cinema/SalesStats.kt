package cinema

import com.fasterxml.jackson.annotation.JsonProperty

class SalesStats(rows: Int, columns: Int) {

    private var currIncome = 0
    private var noOfSeatsAvailable = rows * columns
    private var noOfTicketsSold = 0

    @JsonProperty("current_income")
    fun getCurrentIncome(): Int {
        return currIncome
    }

    @JsonProperty("number_of_available_seats")
    fun getNumberOfAvailableSeats(): Int {
        return noOfSeatsAvailable
    }

    @JsonProperty("number_of_purchased_tickets")
    fun getNumberOfPurchasedTickets(): Int {
        return noOfTicketsSold
    }

    fun saleTicket(seat: PricedSeat) {
        currIncome += seat.price
        noOfSeatsAvailable -= 1
        noOfTicketsSold += 1
    }

    fun returnTicket(seat: PricedSeat) {
        currIncome -= seat.price
        noOfSeatsAvailable += 1
        noOfTicketsSold -= 1
    }
}