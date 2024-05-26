package kotlinBLS

import kotlinBLS.elementsGUI.ButtonGui
import kotlinBLS.elementsGUI.LedGUI
import kotlinx.coroutines.runBlocking

fun main() = runBlocking()
{
    println("START")

    val led1 = LedGUI("led2", 100, 50)
    val button1 =  ButtonGui("click")

    val led = LedActor ("led", led1)
    ActorsMap.mapActor[led.name] = led   // "[]=" equivale a ".put(led.name, led)"
    val control = ControlActor ("control", led.name)
    ActorsMap.mapActor[control.name] = control
    val button  = ButtonActor ("button", button1, control.name)
    ActorsMap.mapActor[button.name] = button

    println("END")
}