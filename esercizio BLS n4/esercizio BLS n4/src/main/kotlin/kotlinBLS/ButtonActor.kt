package kotlinBLS

import it.unibo.bls.devices.gui.ButtonAsGui
import it.unibo.bls.interfaces.IObserver
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import kotlinx.coroutines.*
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.JFrame
import javax.swing.JToggleButton
import javax.swing.plaf.basic.BasicOptionPaneUI

class ButtonActor(name: String, nameControlDest: String, controlActor: ControlActor) : ActorBasic(name)
{
    private val button : ButtonAsGui =  ButtonAsGui.createButton("click")
//    private val toggleButton = JToggleButton()
    private val nameButton = name
    private val nameControl = nameControlDest
    private val controlActor = controlActor
    private val click = "ClickOn"

    init
    {
        println("$nameButton avviato...")

//        val jFrame = JFrame()
//        jFrame.setSize(200, 100)
//        jFrame.layout = GridLayout(1, 2)
//        jFrame.isVisible = true
//        jFrame.add(toggleButton)

        button.addObserver(ButtonObserver(nameButton, this, click))
    }

    override suspend fun actorBody(msg: ApplMessage)
    {
        println("${msg.msgReceiver()}: ricevuto messaggio ${msg.msgContent()} da ${msg.msgSender()}")

        when (msg.msgContent())
        {
            "ClickOn" -> {
                val msgOn = ApplMessage("cmdButton", "dispacth", nameButton, nameControl, "ButtonOn", "1")
                println("${msgOn.msgSender()}: inviato messaggio ${msgOn.msgContent()} a ${msgOn.msgReceiver()}")
                controlActor.getChannel().send(msgOn)
            }

            "ClickOff" -> {
                val msgOff = ApplMessage("cmdButton", "dispacth", nameButton, nameControl, "ButtonOff", "1")
                println("${msgOff.msgSender()}: inviato messaggio ${msgOff.msgContent()} a ${msgOff.msgReceiver()}")
                controlActor.getChannel().send(msgOff)
            }

            else -> println("actor $nameButton | received unknown $msg")
        }
    }
}

class ButtonObserver (nameButton: String, actor: ButtonActor, click: String) : IObserver
{
    private val nameButton = nameButton
    private val actorButton = actor
    private var click = click

    override fun update(p0: Observable?, p1: Any?)
    {
        val msg = ApplMessage("cmdButton", "dispacth", "buttonObserver", nameButton, click, "1")

        if (click == "ClickOn")
            click = "ClickOff"
        else
            click = "ClickOn"

        GlobalScope.launch {
            println("${msg.msgSender()}: inviato messaggio ${msg.msgContent()} a ${msg.msgReceiver()}")
            actorButton.getChannel().send(msg)
        }
    }
}