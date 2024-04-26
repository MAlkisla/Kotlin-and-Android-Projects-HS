package cinema

import com.fasterxml.jackson.annotation.JsonProperty

class Cinema(noOfRows: Int, noOfColumns: Int) {
    @JsonProperty("total_rows")
    val rows = noOfRows

    @JsonProperty("total_columns")
    val columns = noOfColumns

    @JsonProperty("available_seats")
    val seats = buildList {
        for (r in 1..rows) {
            for (c in 1..columns) {
                add(PricedSeat(r, c, if (r > 4) 8 else 10))
            }
        }
    }.toMutableList()
}