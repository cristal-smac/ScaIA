// Copyright (C) Maxime MORGE 2017
package org.scaia.actor.coalition

import akka.actor.{Actor, ActorRef, Stash}
import org.scaia.actor._
import org.scaia.asia._
import org.scaia.solver.asia.{Egalitarian, SocialRule, Utilitarian}

/**
  * Agent representing a coalition in the inclusive procedure (at most one individual is excluded when the coalition is full)
  * @param a activity
  * @param approximation true if only subgroups of size -1 are investigated
  * @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  * */
class InclusiveCoalitionAgent(a: Activity, approximation: Boolean, rule: SocialRule) extends CoalitionAgent(a: Activity, approximation: Boolean, rule: SocialRule)  {


  override def disposing(): Receive = {
    case Propose(i) => {
      adr += (i -> sender)
      val lastProposer = i
      if (debug) log.debug(a.name+" receives a proposal from "+i)
      if (a.c > g.size) {
        if (debug) log.debug("Since the current group of " + a.name + " is undersubscribed " + lastProposer + " is assigned to the activity " + a.name)
        g += lastProposer
        sender ! Accept
        context.become(disposing())
      } else {
        val ng = g + lastProposer
          if (debug) log.debug("The quota of " + a.name + " is reached")
          val waitingReplies = query(ng)
          context.become(casting(lastProposer,waitingReplies))
      }
    }
    case Stop => {
      context.stop(self)
    }
    case Confirm => // Deprecated Confirm
    case msg@_ => log.debug(a.name + " in state disposing receives a message which was not expected: " + msg)
  }


  override def casting(lastProposer: String, waitingReplies: Int): Receive = {
    case Propose(_) => stash()
    case Reply(group, activity, utility) => {
      utilities += (rule match {
        case Utilitarian => (group -> (utilities(group) + utility))
        case Egalitarian => (group -> math.min(utilities(group),utility))
      })
      val ng = g + lastProposer
      if (waitingReplies == 1) {
        if (debug) log.debug(a.name + " evaluates the utilities of the potential subgroups")
        var umax = Double.MinValue
        var bg = Set[String]()
        var subgroups= Set[Set[String]]()
        if (approximation){
          ng.foreach { j =>
            val s = ng.filterNot(_.equals(j))
            subgroups += s
          }
        }
        else subgroups= ng.subsets().filterNot(sg => sg.isEmpty || sg.equals(ng)).toSet
        if (a.c > g.size) {
          if (debug) log.debug("The quota of " + a.name + " is reached")
          subgroups += ng//we consider also the increasing of
        }
        subgroups.foreach { sg =>
          val u = utilities(sg)

          if (debug) log.debug("The utility of the subgroup " + sg + " is " + u)
          if (umax < u) {
            umax = u
            bg = sg
          }
        }
        if (debug) log.debug(bg.toString + " is the best subgroup of " + ng.toString)
        var waitingConfirms = 0
        (g diff bg).foreach { j =>
          if (debug) log.debug(j + " is disassigned from " + a.name)
          adr(j) ! Withdraw
          g -= j
          waitingConfirms += 1
        }
        if (!bg.contains(lastProposer)) {
          if (debug) log.debug(a.name + " rejects the proposal from " + lastProposer)
          adr(lastProposer) ! Reject
          unstashAll()
          context.become(disposing())
        }else{
          if (waitingConfirms!=0) context.become(firing(lastProposer,waitingConfirms))
          else {
            g += lastProposer
            adr(lastProposer) ! Accept
            unstashAll()
            context.become(disposing())
          }
        }
      } else context.become(casting(lastProposer,waitingReplies - 1))
    }
    case Confirm => // Deprecated Confirm
    case msg@_ => log.debug(a.name + " in state casting receives a message which was not expected: " + msg)
  }

  override def firing(lastProposer: String, waitingConfirms: Int): Receive = {
    case Propose(_) => stash()
    case Confirm => {
      if (waitingConfirms == 1) {
        if (debug) log.debug(a.name + " accepts the proposal from " + lastProposer)
        g += lastProposer
        adr(lastProposer) ! Accept
        unstashAll()
        context.become(disposing())
      } else context.become(firing(lastProposer,waitingConfirms - 1))
    }
    case msg@_ => log.debug(a.name + " in state firing receives a message which was not expected: " + msg)
  }

}