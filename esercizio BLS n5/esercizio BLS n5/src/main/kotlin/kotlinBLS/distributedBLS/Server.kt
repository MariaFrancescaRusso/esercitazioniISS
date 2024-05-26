package kotlinBLS.distributedBLS

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import it.unibo.supports.FactoryProtocol
import kotlinBLS.ActorsMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Server(name: String, protocol: String, port: Int, nameControlDest: String) : ActorBasic(name)
{
    private val port = port
    private val controlActor = ActorsMap.mapActor[nameControlDest]

    private var factoryProtocol: FactoryProtocol? = null

    init
    {
//        System.setProperty("inputTimeOut", "600000")  //10 minuti
        when( protocol )
        {
            "TCP" , "UDP" -> {
                factoryProtocol = FactoryProtocol(null, protocol, "Server")
            }

            "SERIAL" -> println("MsgUtil WARNING: TODO")

            else -> println("protocol $protocol unknown!")
        }

        GlobalScope.launch(Dispatchers.IO) {
            val msgAuto = ApplMessage(name, "dispacth", name, name, "Start", "1")
            println("${msgAuto.msgSender()}: inviato messaggio ${msgAuto.msgContent()} a ${msgAuto.msgReceiver()}")
            autoMsg(msgAuto)
        }
    }

    override suspend fun actorBody(msg: ApplMessage)
    {
        println("${msg.msgReceiver()}: ricevuto messaggio ${msg.msgContent()} da ${msg.msgSender()}")
        //ricevo msg da Proxy
        //invio msg a Control

        try {
            while (true)
            {
                println("Server: wait for connection...")

                val connection = factoryProtocol!!.createServerProtocolSupport(port)

                println("Server: handling new connection $connection...")

                while (true)
                {
                    val msgRec = connection.receiveALine()
                    println("Server: ricevuto messaggio $msgRec da ${msg.msgSender()}")

                    val inputmsg = ApplMessage(msgRec)
                    controlActor!!.actor.send(inputmsg)
                }
            }
        } catch (e: Exception) {
            println("Server | WARNING: ${e.message}")
        }
    }
}