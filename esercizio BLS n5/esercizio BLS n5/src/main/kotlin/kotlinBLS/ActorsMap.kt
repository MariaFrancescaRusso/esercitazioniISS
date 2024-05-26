package kotlinBLS

import it.unibo.kactor.ActorBasic

object ActorsMap
{
    val mapActor : MutableMap<String, ActorBasic> = mutableMapOf()

    val serverPort = 8012
}