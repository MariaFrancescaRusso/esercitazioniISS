package kotlinBLS

import it.unibo.bls.interfaces.ILed
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import kotlinx.coroutines.delay

class LedActor(name: String, ledGUI: ILed) : ActorBasic(name)
{
    private val led1 = ledGUI

    init
    {
        println("$name avviato...")
    }


    override suspend fun actorBody(msg: ApplMessage)
    {
        println("${msg.msgReceiver()}: ricevuto messaggio ${msg.msgContent()} da ${msg.msgSender()}")

        when (msg.msgContent())
        {
            "ledOn" -> led1.turnOn()

            "ledOff" -> led1.turnOff()

            else -> println("actor $name | received unknown $msg")
        }
    }

    /** override in kotlin è obbligatorio a differenza di java **/
    override fun toString(): String
    {
        return "actor $name"
    }
}