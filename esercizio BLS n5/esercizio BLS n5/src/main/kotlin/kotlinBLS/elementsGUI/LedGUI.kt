package kotlinBLS.elementsGUI

import it.unibo.chain.segment7.LedSegment
import it.unibo.chain.segment7.LedSegmHorizontal
import java.awt.GridLayout
import javax.swing.JFrame

class LedGUI(myname: String = "led", width: Int = 100, height: Int = 50) : LedSegment(myname, width, height)
{
    private val jFrame = JFrame()
    var numLed = 1
    val led : LedSegmHorizontal

    init
    {
        led = LedSegmHorizontal(myname, width, height)

        /* create JFrame */
        jFrame.setSize(150*numLed, 80)
        jFrame.layout = GridLayout(1, 2)
        jFrame.isVisible = true
        jFrame.add(led)
    }

    override fun turnOn() {
        led.turnOn()
    }

    override fun turnOff() {
        led.turnOff()
    }
}