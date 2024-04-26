package cinema

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

const val ERROR_MSG_SEAT_OUT_OF_BOUNDS = "The number of a row or a column is out of bounds!"
const val ERROR_MSG_SEAT_TAKEN = "The ticket has been already purchased!"
const val ERROR_MSG_WRONG_TOKEN = "Wrong token!"
const val ERROR_MSG_WRONG_PASSWORD = "The password is wrong!"
const val NO_OF_ROWS = 9
const val NO_OF_COLUMNS = 9
const val SECRET_KEY = "super_secret"

@RestController
class Controller {
    private val cinema: Cinema = Cinema(NO_OF_ROWS, NO_OF_COLUMNS)
    private val soldSeats: MutableList<SoldSeat> = mutableListOf()
    private val salesStats = SalesStats(NO_OF_ROWS, NO_OF_COLUMNS)

    @GetMapping("/seats")
    fun getInfo(): Cinema {
        return cinema
    }

    @PostMapping("purchase")
    fun bookTicket(@RequestBody seat: Seat): ResponseEntity<Any> {

        // check if request out of bounds
        if (seat.row !in 1..cinema.rows || seat.column !in 1..cinema.columns) {
            return ResponseEntity(
                object {
                    val error = ERROR_MSG_SEAT_OUT_OF_BOUNDS
                }, HttpStatus.BAD_REQUEST
            )
        }

        // check if seat is already taken
        val retrievedSeat =
            cinema.seats.find { it.row == seat.row && it.column == seat.column } ?: return ResponseEntity(
                object {
                    val error = ERROR_MSG_SEAT_TAKEN
                }, HttpStatus.BAD_REQUEST
            )

        // all seems ok, let's purchase the ticket and generate token
        val token = UUID.randomUUID().toString()
        val soldSeat = SoldSeat(token, retrievedSeat)
        cinema.seats.remove(retrievedSeat)
        salesStats.saleTicket(retrievedSeat)
        soldSeats.add(soldSeat)
        return ResponseEntity(soldSeat, HttpStatus.OK)
    }

    @PostMapping("return")
    fun returnTicket(@RequestBody token: SoldSeat): ResponseEntity<Any> {

        // check if token exists
        val soldSeat = soldSeats.find { it.token == token.token && it.ticket != null }
            ?: return ResponseEntity(object {
                val error = ERROR_MSG_WRONG_TOKEN
            }, HttpStatus.BAD_REQUEST)

        soldSeats.remove(soldSeat)
        salesStats.returnTicket(soldSeat.ticket!!) // safety ensured above
        cinema.seats.add(soldSeat.ticket)
        return ResponseEntity(object {
            @JsonProperty("returned_ticket")
            val ticket = soldSeat.ticket
        }, HttpStatus.OK)
    }

    @GetMapping("stats")
    fun getStatistics(@RequestParam password: String?): ResponseEntity<Any> {
        // Check if password is correct
        return if (password == SECRET_KEY) {
            // Password is correct, return statistics
            val stats = mapOf(
                "current_income" to salesStats.getCurrentIncome(),
                "number_of_available_seats" to salesStats.getNumberOfAvailableSeats(),
                "number_of_purchased_tickets" to salesStats.getNumberOfPurchasedTickets()
            )
            ResponseEntity(stats, HttpStatus.OK)
        } else {
            // Password is incorrect, return error message with 401 status code
            ResponseEntity(mapOf("error" to ERROR_MSG_WRONG_PASSWORD), HttpStatus.UNAUTHORIZED)
        }
    }
}