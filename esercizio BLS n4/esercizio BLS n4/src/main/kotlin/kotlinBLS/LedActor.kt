package kotlinBLS

import it.unibo.bls.devices.gui.segm7.LedSegmentHorizontal
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import kotlinx.coroutines.delay
import java.awt.GridLayout
import javax.swing.JFrame

class LedActor(name: String) : ActorBasic(name)
{
    private val led1 : LedSegmentHorizontal = LedSegmentHorizontal("led1", 100, 50)
//    private val led2 : LedSegmentHorizontal = LedSegmentHorizontal("led1", 100, 50)
//    private val led3 : LedSegmentHorizontal = LedSegmentHorizontal("led1", 100, 50)

    init
    {
        /* create JFrame */
        val numLed = 1
        val jFrame = JFrame()
        jFrame.setSize(150*numLed, 80)
        jFrame.layout = GridLayout(1, 2)
        jFrame.isVisible = true
        jFrame.add(led1)
//        jFrame.add(led2)
//        jFrame.add(led3)

        println("$name avviato...")
    }


    override suspend fun actorBody(msg: ApplMessage)
    {
        println("${msg.msgReceiver()}: ricevuto messaggio ${msg.msgContent()} da ${msg.msgSender()}")

        when (msg.msgContent())
        {
            "ledOn" -> {
                led1.turnOn()
                delay(250)
//                led2.turnOn()
//                delay(250)
//                led3.turnOn()
//                delay(250)
            }
            "ledOff" -> {
                led1.turnOff()
                delay(250)
//                led2.turnOff()
//                delay(250)
//                led3.turnOff()
//                delay(250)
            }
            else -> println("actor $name | received unknown $msg")
        }
    }

    /** override in kotlin è obbligatorio a differenza di java **/
    override fun toString(): String
    {
        return "actor $name"
    }
}