import kotlin.system.exitProcess

data class Car(val number: String, val color: String)

class ParkingSlot(private val id: Int) {
    private var car: Car? = null

    fun isFree(): Boolean = car == null

    fun park(car: Car) {
        this.car = car
        println("${car.color} car parked in spot $id.")
    }

    fun leave() {
        if (isFree()) {
            println("There is no car in spot $id.")
        } else {
            this.car = null
            println("Spot $id is free.")
        }
    }

    fun getCar(): Car? = car

    override fun toString(): String = "$id ${car?.number} ${car?.color}"
}

class ParkingManager {
    private var parkingSlots: List<ParkingSlot>? = null

    fun createParkingSlots(size: Int) {
        if (size <= 0) {
            println("Number of spots should be positive.")
            return
        }
        parkingSlots = List(size) { index -> ParkingSlot(index + 1) }
        println("Created a parking lot with $size spots.")
    }

    fun parkCar(carSpecification: String) {
        if (parkingSlots == null) {
            println("Sorry, a parking lot has not been created.")
            return
        }
        val carDetails = carSpecification.substringAfter("park ").trim()
        val carDetailsArray = carDetails.split(' ')
        if (carDetailsArray.size != 2) {
            println("Invalid car details.")
            return
        }
        val car = Car(carDetailsArray[0], carDetailsArray[1])
        val freeSlot = parkingSlots!!.find { it.isFree() }
        if (freeSlot == null) {
            println("Sorry, the parking lot is full.")
        } else {
            freeSlot.park(car)
        }
    }

    fun carLeaveParking(spotId: Int) {
        if (parkingSlots == null) {
            println("Sorry, a parking lot has not been created.")
            return
        }
        if (spotId <= 0 || spotId > parkingSlots!!.size) {
            println("Invalid spot ID.")
            return
        }
        parkingSlots!![spotId - 1].leave()
    }

    fun printStatus() {
        if (parkingSlots == null) {
            println("Sorry, a parking lot has not been created.")
            return
        }
        val occupiedSpots = parkingSlots!!.filter { !it.isFree() }
        if (occupiedSpots.isEmpty()) {
            println("Parking lot is empty.")
        } else {
            occupiedSpots.forEach { println(it) }
        }
    }

    fun regByColor(color: String) {
        if (parkingSlots == null) {
            println("Sorry, a parking lot has not been created.")
            return
        }
        val cars = parkingSlots!!.filter { !it.isFree() && it.getCar()?.color.equals(color, ignoreCase = true) }
                .map { it.getCar()!!.number }
        if (cars.isEmpty()) {
            println("No cars with color $color were found.")
        } else {
            println(cars.joinToString(", "))
        }
    }

    fun spotByColor(color: String) {
        if (parkingSlots == null) {
            println("Sorry, a parking lot has not been created.")
            return
        }
        val spots = parkingSlots!!.filter { !it.isFree() && it.getCar()?.color.equals(color, ignoreCase = true) }
                .map { it.toString().split(' ')[0] }
        if (spots.isEmpty()) {
            println("No cars with color $color were found.")
        } else {
            println(spots.joinToString(", "))
        }
    }

    fun spotByReg(regNumber: String) {
        if (parkingSlots == null) {
            println("Sorry, a parking lot has not been created.")
            return
        }
        val spot = parkingSlots!!.indexOfFirst { !it.isFree() && it.getCar()?.number.equals(regNumber, ignoreCase = true) }
        if (spot == -1) {
            println("No cars with registration number $regNumber were found.")
        } else {
            println(spot + 1)
        }
    }
}

fun main() {
    val parkingManager = ParkingManager()
    while (true) {
        val input = readLine() ?: ""
        val inputArray = input.split(' ')
        when (inputArray.first()) {
            "park" -> parkingManager.parkCar(input)
            "leave" -> {
                val spotId = inputArray.last().toIntOrNull()
                if (spotId != null) {
                    parkingManager.carLeaveParking(spotId)
                } else {
                    println("Invalid spot ID.")
                }
            }
            "create" -> {
                val size = inputArray.last().toIntOrNull()
                if (size != null) {
                    parkingManager.createParkingSlots(size)
                } else {
                    println("Invalid size.")
                }
            }
            "status" -> parkingManager.printStatus()
            "reg_by_color" -> {
                val color = inputArray.last().toUpperCase()
                parkingManager.regByColor(color)
            }
            "spot_by_color" -> {
                val color = inputArray.last().toUpperCase()
                parkingManager.spotByColor(color)
            }
            "spot_by_reg" -> {
                val regNumber = inputArray.last()
                parkingManager.spotByReg(regNumber)
            }
            "exit" -> exitProcess(0)
            else -> println("Invalid command.")
        }
    }
}
