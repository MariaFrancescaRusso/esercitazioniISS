package kotlinBLS

import kotlinx.coroutines.runBlocking

class MainBLSKotlin {
}

fun main() = runBlocking()
{
    println("START")

    val led = LedActor ("led")
    val control = ControlActor ("control", led.name, led)
    val button  = ButtonActor ("button", control.name, control)

    println("END")
}