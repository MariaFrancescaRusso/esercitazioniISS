package kotlinBLS.distributedBLS

import kotlinBLS.*
import kotlinBLS.elementsGUI.LedChain
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class Node2
{
    val chainled : LedChain

    init
    {
        /** Led Actor with single led **/
//        val led1 = LedGUI()
//        val led = LedActor ("led", led1)
//        ActorsMap.mapActor[led.name] = led   // "[]=" equivale a ".put(led.name, led)"

        /** Led Actor with chain of led **/
        chainled = LedChain()
        val ledChain = LedActor("chainled", chainled)
        ActorsMap.mapActor[ledChain.name] = ledChain

        /** Control Actor **/
//        val control = ControlActor ("control", led.name)
        val control = ControlActor ("control", ledChain.name)
        ActorsMap.mapActor[control.name] = control

        /** Server Actor **/
        val server = Server("server", "TCP", ActorsMap.serverPort, control.name)
        ActorsMap.mapActor[server.name] = server

        /** add a possibility to add or remove a led of the chain in every time **/
        addRemoveLedChain()
    }

    private fun addRemoveLedChain()
    {
        val char: Char

        println("If you want to add or remove a led in chain, please enter 'A' for 'add' or 'R' for 'remove': ")
        val br = BufferedReader(InputStreamReader(System.`in`))

        try
        {
            char = br.readLine().first()

            if (char == 'a' || char == 'A')
                chainled.addLedSegmentToList()      //to add a lad to the chain
            else
                if (char == 'r' || char == 'R')
                    chainled.removeLedSegmentToList()   //to remove a lad to the chain
                else
                    println("Error: $char is not correct!")
        }
        catch (e: IOException)
        {
            System.err.println("Error: $e")
            addRemoveLedChain()
        }

        addRemoveLedChain()
    }
}

fun main() = runBlocking {
    println("BLS Distribuito Node 2 | START thread = ${Thread.currentThread().name}")

    Node2()

    println("BLS Distribuito Node 2 | END")
}
