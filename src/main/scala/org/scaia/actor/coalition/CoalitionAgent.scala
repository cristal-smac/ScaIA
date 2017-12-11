// Copyright (C) Maxime MORGE 2017
package org.scaia.actor.coalition

import akka.actor.{Actor, ActorRef, Stash}
import akka.event.Logging
import org.scaia.actor._
import org.scaia.asia._
import org.scaia.solver.asia.{Egalitarian, SocialRule, Utilitarian}

/**
  * Agent representing a coalition in the selective procedure (at each integration some individuals can be excluded)
  * @param a activity
  * @param approximation true if only subgroups of size -1 are investigated
  * @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  * */
abstract class CoalitionAgent(a: Activity, approximation: Boolean, rule: SocialRule) extends Actor with Stash with akka.actor.ActorLogging {
  val debug = true

  override val log = Logging(context.system.eventStream, "CoalitionAgent")

  var g = Set[String]() // The current set of individuals
  var adr = Map[String, ActorRef]() // The addresses of the individual agents
  var utilities = Map[Set[String], Double]() // The utilities of the groups

  /**
    * Query the individuals in ng  about the utilities in the subgroups of g
    * returns the number of expected replies
    */
  def query(group: Set[String]): Int = {
    var waitingReplies = 0
    if (debug) log.debug(a.name + " queries about the utilities of the potential subgroups of "+group.toString)
    var subgroups= Set[Set[String]]()
    if (approximation) {
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
        case Egalitarian => utilities += (sg -> Double.MaxValue)
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
    if (debug) log.debug(a.name + " queries about the utilities of the potential subgroups of "+group.toString)
    var subgroups= Set[Set[String]]()
    if (approximation) {
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
  def receive(): Receive = disposing()

  def disposing(): Receive

  def casting(lastProposer: String, waitingReplies: Int): Receive

  def firing(lastProposer: String, waitingConfirms: Int): Receive

}