package kotlinBLS

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import kotlinx.coroutines.delay

class ControlActor(name: String, nameLedDest: String) : ActorBasic(name)
{
    private val nameControl = name
    private val nameLed = nameLedDest
    private val ledActor = ActorsMap.mapActor[nameLedDest]  // [] equivale a .get(nameLedDest)  //:ActorBasic?
    private var button = ""

    init
    {
        println("$nameControl avviato...")
    }

    override suspend fun actorBody(msg: ApplMessage)
    {
        println("${msg.msgReceiver()}: ricevuto messaggio ${msg.msgContent()} da ${msg.msgSender()}")

        if(ledActor is ActorBasic) {
            if (msg.msgContent() == "ButtonOn")  /* blink led */ {
                val msgOn = ApplMessage(nameControl, "dispacth", nameControl, nameLed, "ledOn", "1")
                println("${msgOn.msgSender()}: inviato messaggio ${msgOn.msgContent()} a ${msgOn.msgReceiver()}")
                ledActor.getChannel().send(msgOn)

                delay(500)

                val msgOff = ApplMessage(nameControl, "dispacth", nameControl, nameLed, "ledOff", "1")
                println("${msgOff.msgSender()}: inviato messaggio ${msgOff.msgContent()} a ${msgOff.msgReceiver()}")
                ledActor.getChannel().send(msgOff)

                delay(500)

                button = msg.msgContent() //"ButtonOn"

                val msgAutoOn = ApplMessage(nameControl, "dispacth", nameControl, nameControl, "ControlButtonOn", "1")
                println("${msgAutoOn.msgSender()}: inviato messaggio ${msgAutoOn.msgContent()} a ${msgAutoOn.msgReceiver()}")
                autoMsg(msgAutoOn)
            }

            if (msg.msgContent() == "ButtonOff") /* turnOff led */ {
                val msgOff = ApplMessage(nameControl, "dispacth", nameControl, nameLed, "ledOff", "1")
                println("${msgOff.msgSender()}: inviato messaggio ${msgOff.msgContent()} a ${msgOff.msgReceiver()}")
                ledActor.getChannel().send(msgOff)

                button = msg.msgContent() //"ButtonOff"
            }

            if (msg.msgContent() == "ControlButtonOn" && button == "ButtonOn") {
                val msgOn = ApplMessage(nameControl, "dispacth", nameControl, nameLed, "ledOn", "1")
                println("${msgOn.msgSender()}: inviato messaggio ${msgOn.msgContent()} a ${msgOn.msgReceiver()}")
                ledActor.getChannel().send(msgOn)

                delay(500)

                val msgOff = ApplMessage(nameControl, "dispacth", nameControl, nameLed, "ledOff", "1")
                println("${msgOff.msgSender()}: inviato messaggio ${msgOff.msgContent()} a ${msgOff.msgReceiver()}")
                ledActor.getChannel().send(msgOff)

                delay(500)

                val msgAutoOn = ApplMessage(nameControl, "dispacth", nameControl, nameControl, "ControlButtonOn", "1")
                println("${msgAutoOn.msgSender()}: inviato messaggio ${msgAutoOn.msgContent()} a ${msgAutoOn.msgReceiver()}")
                autoMsg(msgAutoOn)
            }
        }
    }
}