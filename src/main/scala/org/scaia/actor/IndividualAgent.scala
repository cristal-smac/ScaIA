// Copyright (C) Maxime MORGE 2017
package org.scaia.actor

import akka.actor.{Actor, ActorRef}
import org.scaia.asia._

/**
  * Agent representing an individual agent
  * @param individual the individual agent
  * */
class IndividualAgent(individual: Individual) extends Actor{
  val debug = false

  var solver : ActorRef = _
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
      solver = sender
      if (!concessions.isEmpty){
        if (debug) println(s"$i proposes itself to "+concessions.head)
        addresses(concessions.head) ! Propose(i)
      }
      else {
        if (debug) println(s"$i stays inactive")
        solver ! Assignement(i,  Activity.VOID.name)
      }
    }
    case Accept => { // Assignment is confirmed
      solver ! Assignement(i, concessions.head)
    }
    case Reject => { // Assignment is disconfirmed
      concessions = concessions.tail
      if (concessions.isEmpty) {//Either all concessions are made and i is inactive
        solver ! Assignement(i, Activity.VOID.name)
      }else {//Or concession is made
        addresses(concessions.head) ! Propose(i)
      }
    }
    case Withdraw => { // Assignment is withdrawn
      solver ! Disassignement(i)
    }
    case Confirm => { // Disassignement has been taken into account by the solver
      addresses(concessions.head) ! Confirm
      concessions = concessions.tail
      if (concessions.isEmpty){// Either all concessions are made and i is inactive
        solver ! Assignement(i, Activity.VOID.name)
      } else {// Or concession is made
          if (debug) println(s"$i proposes itself to "+concessions.head)
          addresses(concessions.head) ! Propose(i)
      }
    }
    case Query(group,activity) => {//Utility is requested
      sender ! Reply(group,activity,individual.u(group,activity))
    }
  }

  /**
    * Handles management messages
    */
  def handleManagement(): Receive = {
    case Inform(adr: Map[String,ActorRef]) => { // Receive white page
      this.addresses=adr
      this.concessions=buildConcessions(adr.keySet)
      if (debug) println(s"$i's list of concessions: "+concessions)
    }
    case Stop => { // Agent is stopped
      context.stop(self)
    }
    case msg@_ => println(s"$i receives a message which was not expected: "+msg)
  }

  /**
    * Return the list of concessions, i.e. the list of attractive activities by preference order
    */
  def buildConcessions(activityNames: Set[String]) : List[String] = activityNames.filter(a =>individual.v(a)>=0).toList.sortWith((left, right) => individual.v(left) > individual.v(right))

}