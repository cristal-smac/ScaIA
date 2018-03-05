// Copyright (C) Maxime MORGE 2017
package org.scaia.actor.coalition

import akka.actor.{Actor, ActorRef, Stash}
import org.scaia.actor._
import org.scaia.asia._
import org.scaia.solver.asia.{Egalitarian, SocialRule, Utilitarian}

/**
  * Agent representing a coalition in the inclusive procedure (at most one individual is excluded when the coalition is full)
  * @param a activity
  * @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  * */
class InclusiveCoalitionAgent(a: Activity, rule: SocialRule) extends CoalitionAgent(a: Activity, rule: SocialRule) with Stash{

  /**
    *   The coalition agent is waiting for new proposals
    */
  override def disposing(): Receive = {
    case Propose(i) => {
      adr += (i -> sender)
      if (debug) log.debug(s"${a.name} receives a proposal from $i")
      if (a.c > g.size) {
        if (debug) log.debug(s"Since the capacity of ${a.name} is not reached, $i is assigned to the activity ${a.name}")
        g += i
        sender ! Accept
        context.become(disposing())
      } else {
        val ng = g + i
        if (debug) log.debug(s"Since the capacity of ${a.name} is reached, the coalition agent is looking for the best subgroup")
        val waitingReplies =  query(subgroups(g+i, g.size, g.size))
        context.become(casting(i,waitingReplies))
      }
    }
    case Stop => context.stop(self)
    case Confirm => // Deprecated Confirm
    case msg@_ => log.debug("${a.name} in state disposing receives a message which was not expected: $msg")
  }


}