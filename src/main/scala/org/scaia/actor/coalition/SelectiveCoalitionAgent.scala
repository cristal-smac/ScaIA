// Copyright (C) Maxime MORGE 2017
package org.scaia.actor.coalition

import akka.actor.{Actor, ActorRef, Stash}
import org.scaia.actor._
import org.scaia.asia._
import org.scaia.solver.asia.{Egalitarian, SocialRule, Utilitarian}

/**
  * Agent representing a coalition in the selective matching mechanism (at each integration some individuals can be excluded)
  * @param a activity
  * @param approximation true if only subgroups of size -1 are investigated
  * @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  * */
class SelectiveCoalitionAgent(a: Activity, approximation: Boolean, rule: SocialRule) extends  CoalitionAgent(a: Activity, rule: SocialRule) with Stash{

  /**
    *   The coalition agent is waiting for new proposals
    */
  override def disposing(): Receive = {
    case Propose(i) =>
      adr += (i -> sender)
      if (debug) log.debug(s"${a.name} receives a proposal from $i")
      if (g.isEmpty) {
        if (debug) log.debug(s"Since the current group of ${a.name} is empty $i is assigned to the activity ${a.name}")
        g += i
        sender ! Accept
        context.become(disposing())
      } else {
        val ng = g + i
        if (a.c > g.size) {
          if (debug) log.debug(s"The capacity of ${a.name} is not reached")
          val waitingReplies = if (approximation) query(subgroups(g+i, g.size, g.size+1))
          else query(subgroups(g+i, 1, g.size+1))
          context.become(casting(i,waitingReplies))
        } else {
          if (debug) log.debug(s"The capacity of ${a.name} is reached")
          val waitingReplies = if (approximation) query(subgroups(g+i, g.size, g.size))
          else query(subgroups(g+i, 1, g.size))
          context.become(casting(i,waitingReplies))
        }
      }
    case Stop => context.stop(self)
    case Confirm => // Deprecated Confirm
    case msg@_ => log.debug("${a.name} in state disposing receives a message which was not expected: $msg")
  }


}