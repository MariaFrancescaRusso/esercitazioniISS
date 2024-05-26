package kotlinBLS.elementsGUI

import it.unibo.bls.interfaces.ILed
import it.unibo.bls.utils.Utils
import it.unibo.chain.segment7.LedSegmHorizontal
import it.unibo.chain.segment7.LedSegment
import java.awt.GridLayout
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.swing.JFrame

class LedChain : ILed
{
    private var ledState : Boolean
    private var ledSegmentList : ArrayList<LedSegment> = ArrayList()
    private var jFrame = JFrame()
    private var numLed : Int = 0

    init
    {
        this.ledState  = false

        /* request about how many virtual leds in the chain */
        numLed = getNumLed()
        println("Number of horizontal virtual leds is: $numLed")

        /* create JFrame */
        jFrame.setSize(120*numLed, 100)
        jFrame.layout = GridLayout(1, 2)

        /* create virtual leds */
        for (i in 1..numLed)
        {
            val ledName = "led$i"
            val ledSegmHorizontal = LedSegmHorizontal(ledName, 10, 5)

            jFrame.add(ledSegmHorizontal)

            ledSegmentList.add(ledSegmHorizontal)
        }

        jFrame.isVisible = true
    }

    fun getLedSegmentList(): List<LedSegment>? {
        return ledSegmentList
    }

    fun setLedSegmentList(ledSegmentList: ArrayList<LedSegment>)
    {
        this.ledSegmentList = ledSegmentList
    }

    fun  addLedSegmentToList()
    {
        numLed++

        val ledName = "led${numLed}"

        val ledSegmHorizontal = LedSegmHorizontal(ledName, 10, 5)

        jFrame.add(ledSegmHorizontal)
        jFrame.setSize(120*numLed, 100)
        ledSegmentList.add(ledSegmHorizontal)
    }

    fun  removeLedSegmentToList()
    {
        numLed--

        ledSegmentList.removeAt(numLed)
        println("LedSegment led${numLed+1} | REMOVED 10:5")

        jFrame.contentPane.remove(numLed)
        jFrame.setSize(120*numLed, 100)
    }

    override fun turnOn()
    {
        ledState = true

        for (ledSegment in ledSegmentList)
        {
            ledSegment.turnOn()
            println("ledSegment ${ledSegmentList.indexOf(ledSegment) + 1}: turnOn")
            Utils.delay(500)
            ledSegment.turnOff()
        }
    }

    override fun turnOff()
    {
        ledState = false

        for (ledSegment in ledSegmentList)
        {
            ledSegment.turnOff()
            println("ledSegment ${ledSegmentList.indexOf(ledSegment) + 1}: turnOff")
        }
    }

    override fun getState(): Boolean
    {
        return ledState
    }

    private fun getNumLed(): Int
    {
        var numLed: Int

        println("How many horizontal virtual leds do you want in BLSystem? (int >= 3 (default))")
        val br = BufferedReader(InputStreamReader(System.`in`))
        try
        {
            numLed = Integer.parseInt(br.readLine())
        }
        catch (e: IOException)
        {
            System.err.println("Insert a number! By default number of led is 3..")
            numLed = 3
        }
        catch (e: NumberFormatException)
        {
            System.err.println("Insert a number! By default number of led is 3..")
            numLed = 3
        }

        if (numLed < 3)
        {
            System.err.println("The number of leds must be >= 3! By default number of led is 3..")
            numLed = 3
        }

        return numLed
    }
}