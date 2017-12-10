// Copyright (C) Maxime MORGE 2017
package org.scaia.actor.coalition

import akka.actor.{Actor, ActorRef, Stash}
import org.scaia.actor._
import org.scaia.asia._
import org.scaia.solver.asia.{Egalitarian, SocialRule, Utilitarian}

/**
  * Agent representing
  * @param a activity
  * @param restricted true if only subgroups of size -1 are investigated
  * @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  * */
class SelectiveCoalitionAgent(a: Activity, restricted: Boolean, rule: SocialRule) extends Actor with Stash {
  val debug = false

  var g = Set[String]() // The current set of individuals
  var adr = Map[String, ActorRef]() // The adresses of the individual agents
  var utilities = Map[Set[String], Double]() // The utilities of the groups

  //Query the individual about the utilities in the subgroups of g
  //and returns the number of expected replies
  def query(group: Set[String]): Int = {
    var waitingReplies = 0
    if (debug) println(a.name + " queries about the utilities of the potential subgroups of "+group.toString)
    var subgroups= Set[Set[String]]()
    if (restricted) {
      //subgroups= group.subsets().filter(sg => sg.size==(group.size-1) && ! sg.equals(Set()))
      group.foreach { j =>
        val s = group.filterNot(_.equals(j))
        subgroups += s
      }
    }
    else subgroups= group.subsets().filterNot(sg => sg.equals(group) || sg.isEmpty).toSet
    subgroups.foreach { sg =>
      //Initialisation depends on the welfare
      rule match {
        case Utilitarian => utilities += (sg -> 0.0)
        case Egalitarian => utilities += (sg -> 1.0)
      }
      sg.foreach { j =>
        adr(j) ! Query(sg, a.name)
        waitingReplies += 1
      }
    }
    waitingReplies
  }

  //Query the individual about the utilities in the subgroups of g
  //and returns the number of expected replies
  def queryAll(group: Set[String]): Int = {
    var waitingReplies = 0
    if (debug) println(a.name + " queries about the utilities of the potential subgroups of "+group.toString)
    var subgroups= Set[Set[String]]()
    if (restricted) {
      group.foreach { j =>
        val s = group.filterNot(_.equals(j))
        subgroups += s
      }
      subgroups += group
    }
    else subgroups= group.subsets().filterNot(sg => sg.isEmpty).toSet
    subgroups.foreach { sg =>
      //Initialisation depends on the welfare
      rule match {
        case Utilitarian => utilities += (sg -> 0.0)
        case Egalitarian => utilities += (sg -> 1.0)
      }
      sg.foreach { j =>
        adr(j) ! Query(sg, a.name)
        waitingReplies += 1
      }
    }
    waitingReplies
  }


  /**
    * Method invoked when a message is received
    */
  override def receive(): Receive = disposing()

  def disposing(): Receive = {
    case Propose(i) => {
      adr += (i -> sender)
      val lastProposer = i
      if (debug) println(a.name+" receives a proposal from "+i)
      if (g.isEmpty) {
        if (debug) println("Since the current group of " + a.name + " is empty " + lastProposer + " is assigned to the activity " + a.name)
        g += lastProposer
        sender ! Accept
        context.become(disposing())
      } else {
        val ng = g + lastProposer
        if (a.c > g.size) {
          if (debug) println("The quota of " + a.name + " is not reached")
          val waitingReplies = queryAll(ng)
          context.become(casting(lastProposer,waitingReplies))
        } else {
          if (debug) println("The quota of " + a.name + " is reached")
          val waitingReplies = query(ng)
          context.become(casting(lastProposer,waitingReplies))
        }
      }
    }
    case Stop => {
      context.stop(self)
    }
    case Confirm => // Deprecated Confirm
    case msg@_ => println(a.name + " in state disposing receives a message which was not expected: " + msg)
  }


  def casting(lastProposer: String, waitingReplies: Int): Receive = {
    case Propose(_) => stash()
    case Reply(group, activity, utility) => {
      utilities += (rule match {
        case Utilitarian => (group -> (utilities(group) + utility))
        case Egalitarian => (group -> math.min(utilities(group),utility))
      })
      val ng = g + lastProposer
      if (waitingReplies == 1) {
        if (debug) println(a.name + " evaluates the utilities of the potential subgroups")
        var umax = Double.MinValue
        var bg = Set[String]()
        var subgroups= Set[Set[String]]()
        if (restricted){
          ng.foreach { j =>
            val s = ng.filterNot(_.equals(j))
            subgroups += s
          }
        }
        else subgroups= ng.subsets().filterNot(sg => sg.isEmpty || sg.equals(ng)).toSet
        if (a.c > g.size) {
          if (debug) println("The quota of " + a.name + " is reached")
          subgroups += ng//we consider also the increasing of
        }
        subgroups.foreach { sg =>
          val u = utilities(sg)
          if (debug) println("The utility of the subgroup " + sg + " is " + u)
          if (umax < u) {
            umax = u
            bg = sg
          }
        }
        if (debug) println(bg.toString + " is the best subgroup of " + ng.toString)
        var waitingConfirms = 0
        (g diff bg).foreach { j =>
          if (debug) println(j + " is disassigned from " + a.name)
          adr(j) ! Withdraw
          g -= j
          waitingConfirms += 1
        }
        if (!bg.contains(lastProposer)) {
          if (debug) println(a.name + " rejects the proposal from " + lastProposer)
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
    case msg@_ => println(a.name + " in state casting receives a message which was not expected: " + msg)
  }

  def firing(lastProposer: String, waitingConfirms: Int): Receive = {
    case Propose(_) => stash()
    case Confirm => {
      if (waitingConfirms == 1) {
        if (debug) println(a.name + " accepts the proposal from " + lastProposer)
        g += lastProposer
        adr(lastProposer) ! Accept
        unstashAll()
        context.become(disposing())
      } else context.become(firing(lastProposer,waitingConfirms - 1))
    }
    case msg@_ => println(a.name + " in state firing receives a message which was not expected: " + msg)
  }

}