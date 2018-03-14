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
  * @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  * */
abstract class CoalitionAgent(a: Activity, rule: SocialRule) extends Actor with Stash with akka.actor.ActorLogging {
  val debug = false

  override val log = Logging(context.system.eventStream, "CoalitionAgent")

  var g: Set[String] = Set[String]() // The current set of individuals
  var adr: Map[String, ActorRef] = Map[String, ActorRef]() // The addresses of the individual agents
  var welfares: Map[Set[String], Double] = Map[Set[String], Double]() // The utilities of the groups

  /**
    * Returns the subgroups of g with the size between min and max
    * @param min size of the subgroups is 1 or group.size-1
    * @param max size of the subgroups is group.size-1 or group.size
    */
  def subgroups(group: Set[String], min: Int, max: Int) : Set[Set[String]]= {
    if (min != 1){// Remove at most one individual
      var subgroups = Set[Set[String]]()
      group.foreach { j => // Each groupe with a single removed individual
        val s = group.filterNot(_.equals(j))
        subgroups += s
      }
      if (min == max) subgroups
      else subgroups + group
    }
    else // returns all the non-empty subgroups of size <= max
      group.subsets().filter{sg =>
      sg.size >= min && sg.size <= max
    }.toSet
  }

  /**
    * Resets the welfares
    */
  def resetWelfares(): Unit= {
    welfares  = Map[Set[String], Double]()
  }

  /**
    * Initiates the internal representation of welfare
    * @param group to be initiated
    */
  def initWelfare(group: Set[String]): Unit = {
    rule match { //
      case Utilitarian => welfares += (group -> 0.0)
      case Egalitarian => welfares += (group -> Double.MaxValue)
    }
  }

  /**
    * Updates the welfare
    * @param group to be updated
    * @param welfare to be recorded
    */
  def updateWelfare(group: Set[String], welfare: Double): Unit = {
    welfares += (rule match {
      case Utilitarian => group -> (welfares(group) + welfare)
      case Egalitarian => group -> math.min(welfares(group), welfare)
    })
  }

  /**
    * Query the individuals in ng  about the utilities in the subgroups of g
    * @param groups to be queried
    * returns the number of expected replies
    */
  def query(groups: Set[Set[String]]): Int = {
    var waitingReplies = 0
    resetWelfares() // Reset the welfares
    groups.foreach{ group =>
      if (debug) log.debug(s"${a.name} queries about the welfare of the potential group of $group")
      initWelfare(group)// Initiate the welfare
      group.foreach{ j => // For each member of the potential group
        adr(j) ! Query(group, a.name) // Ask for the individual welfare in this new group
        waitingReplies += 1
      }
    }
    waitingReplies
  }

  /**
    * Returns the best subgroup
    */
  def best(): Set[String] = {
    var b = Set[String]()
    var maxWelfare = Double.MinValue
    welfares.foreach{ case (group,welfare) =>
      if (debug) log.debug(s"The welfare of the subgroup $group is $welfare")
      if (welfare > maxWelfare) {
        b = group
        maxWelfare = welfare
      }
    }
  b
  }

  /**
    *   The coalition agent is waiting for reply in order to select the best group
    *   @param i the proposer
    *   @param waitingReplies the number of expected replies
    */
  def casting(i: String, waitingReplies: Int): Receive = {
    case Reply(group, _, welfare) =>
      updateWelfare(group, welfare)
      if (waitingReplies != 1) { // Decrease the number of expected replies
        context.become(casting(i, waitingReplies - 1))
      }
      else { // All the replies have been received
        if (debug) log.debug(s"${a.name} evaluates the welfare of the potential subgroups")
        val bg = best()
        if (debug) log.debug(s"$bg is the best subgroup")
        if (!bg.contains(i)) { // Either i is rejected
          if (debug) log.debug(s"${a.name} rejects the proposer $i")
          adr(i) ! Reject
          unstashAll()
          context.become(disposing())
        } else {
          // Or i is accepted
          if ((g + i).equals(bg)) {
            if (debug) log.debug(s"${a.name} rejects no one")
            g += i
            adr(i) ! Accept
            unstashAll()
            context.become(disposing())
          } else {
            // And there is some rejection
            var waitingConfirms = 0
            (g diff bg).foreach { j =>
              if (debug) log.debug(s"$j is disassigned from ${a.name}")
              adr(j) ! Withdraw
              g -= j
              waitingConfirms += 1
            }
            context.become(firing(i, waitingConfirms))
          }
        }
      }
    case Propose(j) =>
      if (debug) log.debug(s"${a.name} in state casting stashes proposal from $j")
      stash()
    case Confirm => // Deprecated Confirm
    case msg@_ => log.debug(s"${a.name} in state casting receives a message which was not expected: $msg")
  }


  /**
    * The coalition agent in
    * @param proposer the proposer
    * @param waitingConfirms the number of expected confirmations
    */
  def firing(proposer: String, waitingConfirms: Int): Receive = {
    case Confirm =>
      if (waitingConfirms != 1) context.become(firing(proposer,waitingConfirms - 1))
      else { // All the confirmations have been received
        if (debug) log.debug(s"${a.name} accepts the proposal from $proposer")
        g += proposer
        adr(proposer) ! Accept
        unstashAll()
        context.become(disposing())
      }
    case Propose(j) =>
      if (debug) log.debug(s"${a.name} in state firing stashes proposal from $j")
      stash()
    case msg@_ => log.debug(s"${a.name} in state firing receives a message which was not expected: $msg")
  }


  /**
    * Method invoked when a message is received
    */
  def receive(): Receive = disposing()

  def disposing(): Receive

}