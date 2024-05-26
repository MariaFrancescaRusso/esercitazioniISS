package kotlinBLS.distributedBLS

import kotlinBLS.ActorsMap
import kotlinBLS.ButtonActor
import kotlinBLS.elementsGUI.ButtonGui
import kotlinx.coroutines.runBlocking

class Node1
{
    init
    {
        val proxy = Proxy("proxy", "TCP", "localhost", ActorsMap.serverPort)
        ActorsMap.mapActor[proxy.name] = proxy  // "[]=" equivale a ".put(proxy.name, proxy)"

        val button1 = ButtonGui("click")
        val button = ButtonActor("button", button1, proxy.name)
        ActorsMap.mapActor[button.name] = button
    }
}

fun main() = runBlocking {
    println("BLS Distribuito Node 1 | START thread = ${Thread.currentThread().name}")

    Node1()

    println("BLS Distribuito Node 1 | END")
}