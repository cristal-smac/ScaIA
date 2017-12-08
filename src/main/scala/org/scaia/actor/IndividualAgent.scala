// Copyright (C) Maxime MORGE 2017
package org.scaia.actor

import akka.actor.{Actor, ActorRef}
import org.scaia.asia._

/**
  * Agent representing
  * @param i an individual agent
  * */
class IndividualAgent(i: Individual) extends Actor{
  val debug = false

  var solver : ActorRef = _
  var addresses= Map[String,ActorRef]()//White page of the activities
  var concessions = List[String]() // concessions list

  /**
    * Method invoked when a message is received
    */
  def receive = {
    case Inform(addresses) => {//Receive white page
      this.addresses=addresses
      //Build concession list
      concessions=addresses.keys.toList.filter(a =>i.v(a)>=0).sortWith((left, right) => i.v(left) > i.v(right))
      if (debug) println(i.name+" concession list: "+concessions)
    }
    case Start => {//Make a proposal to the best activity
      solver = sender
      if (!concessions.isEmpty){
        if (debug) println(i.name+" proposes itself to "+concessions.head)
        addresses(concessions.head) ! Propose(i.name)
      }
      else {
        if (debug) println(i.name+" stays inactive")
        solver ! Assignement(i.name,  Activity.VOID.name)
      }
    }
    case Accept => {//Assignment is confirmed
      solver ! Assignement(i.name, concessions.head)
    }
    case Reject => {//Assignment is disconfirmed
      concessions = concessions.tail
      if (concessions.isEmpty) {//Either all concessions are made and i is inactive
        solver ! Assignement(i.name, Activity.VOID.name)
      }else {//Or concession is made
        addresses(concessions.head) ! Propose(i.name)
      }
    }
    case Withdraw => {//Assignment is withdrawn
      solver ! Disassignement(i.name)
    }
    case Confirm => {//Disassignement has been taken into account by the solver
      addresses(concessions.head) ! Confirm
      concessions = concessions.tail
      if (concessions.isEmpty){// Either all concessions are made and i is inactive
        solver ! Assignement(i.name, Activity.VOID.name)
      } else {//Or concession is made
          if (debug) println(i.name+" proposes itself to "+concessions.head)
          addresses(concessions.head) ! Propose(i.name)
      }
    }
    case Query(group,activity) => {//Utility is requested
      sender ! Reply(group,activity,i.u(group,activity))
    }
    case Stop => {//Agent is stoppend
      context.stop(self)//stop the activityAgent
    }
    case msg@_ => println(i.name+" receives a message which was not expected: "+msg)
  }
}