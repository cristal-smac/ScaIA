// Copyright (C) Maxime MORGE 2017
package org.scaia.actor

import java.lang.RuntimeException

import akka.actor.{Actor, ActorRef}
import org.scaia.asia._

/**
  * Agent representing an individual agent
  * @param individual the individual agent
  * */
class IndividualAgent(individual: Individual) extends Actor with akka.actor.ActorLogging{
  val debug = false

  var solver = context.parent
  var addresses= Map[String,ActorRef]() // White page of the coalition agent
  var concessions = List[String]() // List of concessions
  val i=individual.name // Name of the agent

  /**
    * Method invoked when a message is received
    */
  def receive(): Receive = behaviour() orElse handleManagement()

  /**
    * The reactive behaviour of the agent
    */
  def behaviour(): Receive = {
    case Start => { // Make a proposal to the best activity
      if (this.isDesesperated){
        if (debug)  log.debug(s"$i definitively stays inactive")
        solver ! Assignement(i,  Activity.VOID.name)
      }
      else {
        if (debug) log.debug(s"$i proposes itself to ${concessions.head}")
        addresses(concessions.head) ! Propose(i)
      }
    }
    case Accept => {
      if (debug) log.debug(s"$i is assigned to ${concessions.head}")
      solver ! Assignement(i, concessions.head)
    }
    case Reject => {
      if (debug) log.debug(s"$i is rejected by ${concessions.head}")
      this.concede()
      if (this.isDesesperated) {//Either all concessions are made and i is inactive
        solver ! Assignement(i, Activity.VOID.name)
      }else {//Or he proposes to the next preferred activiyt
        addresses(this.preferredActiviy()) ! Propose(i)
      }
    }
    case Withdraw => {
      if (debug) log.debug(s"$i is ejected by ${concessions.head}")
      solver ! Disassignement(i)
    }
    case Confirm => {
      if (debug) log.debug(s"$i has received  the confirmation of the dissagnement")
      addresses(preferredActiviy()) ! Confirm
      this.concede()
      if (this.isDesesperated) { // Either all concessions are made and i is inactive
        solver ! Assignement(i, Activity.VOID.name)
      } else { // Or concession is made
          if (debug)  log.debug(s"$i proposes itself to ${concessions.head}")
          addresses(this.preferredActiviy()) ! Propose(i)
      }
    }
    case Query(g,a) => { // Opinion is requested
      sender ! Reply(g,a,individual.u(g,a))
    }
  }

  /**
    * Handles management messages
    */
  def handleManagement(): Receive = {
    case Inform(adr: Map[String,ActorRef]) => { // Receive white page
      this.addresses=adr
      this.concessions=buildConcessions(adr.keySet)
      if (debug)  log.debug(s"$i's list of concessions: $concessions")
    }
    case Stop => { // Agent is stopped
      context.stop(self)
    }
    case msg@_ => log.error(s"$i receives a message which was not expected: $msg")
  }

  /**
    * Return the list of concessions, i.e. the list of attractive activities by preference order
    */
  def buildConcessions(activityNames: Set[String]) : List[String] = activityNames.filter(a =>individual.v(a)>=0).toList.sortWith((left, right) => individual.v(left) > individual.v(right))

  /**
    * Returns true if the agent is deseperated
    */
  def isDesesperated() : Boolean = this.concessions.isEmpty

  /**
    * Returns the most preferred activity in the list of concession
    */
  @throws(classOf[RuntimeException])
  def preferredActiviy() : String = {
    if (this.concessions.isEmpty) {
      log.error(s"$i can no more concede")
      throw new RuntimeException(s"$i can no more concede")
    }
    this.concessions.head
  }

  /**
    * Considers the next
    */
  def concede() : Unit = {
    concessions = concessions.tail
  }
}