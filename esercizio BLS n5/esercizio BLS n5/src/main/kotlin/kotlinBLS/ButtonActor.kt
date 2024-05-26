package kotlinBLS

import it.unibo.bls.interfaces.IObserver
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import kotlinx.coroutines.*
import java.util.*

class ButtonActor(name: String, buttonAsGui: Observable, nameControlDest: String) : ActorBasic(name)
{
    private val button = buttonAsGui
    private val nameButton = name
    private val nameControl = nameControlDest
    private val controlActor = ActorsMap.mapActor.get(nameControlDest)
    private val click = "ClickOn"

    init
    {
        println("$nameButton avviato...")

        button.addObserver(ButtonObserver(nameButton, click))
    }

    override suspend fun actorBody(msg: ApplMessage)
    {
        println("${msg.msgReceiver()}: ricevuto messaggio ${msg.msgContent()} da ${msg.msgSender()}")

        when (msg.msgContent())
        {
            "ClickOn" -> {
                val msgOn = ApplMessage("cmdButton", "dispacth", nameButton, nameControl, "ButtonOn", "1")
                println("${msgOn.msgSender()}: inviato messaggio ${msgOn.msgContent()} a ${msgOn.msgReceiver()}")
                controlActor!!.getChannel().send(msgOn)
            }

            "ClickOff" -> {
                val msgOff = ApplMessage("cmdButton", "dispacth", nameButton, nameControl, "ButtonOff", "1")
                println("${msgOff.msgSender()}: inviato messaggio ${msgOff.msgContent()} a ${msgOff.msgReceiver()}")
                controlActor!!.getChannel().send(msgOff)
            }

            else -> println("actor $nameButton | received unknown $msg")
        }
    }
}

class ButtonObserver (nameButton: String, click: String) : IObserver
{
    private val nameButton = nameButton
    private var actorButton : ActorBasic? = null
    private var click = click

    override fun update(p0: Observable?, p1: Any?)
    {
        val msg = ApplMessage("cmdButton", "dispacth", "buttonObserver", nameButton, click, "1")

        if (click == "ClickOn")
            click = "ClickOff"
        else
            click = "ClickOn"

        if(actorButton !is ActorBasic)
        {
            actorButton = ActorsMap.mapActor[nameButton]    // [] equivale a .get(nameButton)
        }

        GlobalScope.launch {
            println("${msg.msgSender()}: inviato messaggio ${msg.msgContent()} a ${msg.msgReceiver()}")
            actorButton!!.getChannel().send(msg)
        }
    }
}