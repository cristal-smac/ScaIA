// Copyright (C) Maxime MORGE 2017
package org.scaia.actor

import akka.actor.{Actor, ActorRef, Props}
import org.scaia.actor.coalition.InclusiveCoalitionAgent
import org.scaia.asia._
import org.scaia.solver.asia.SocialRule

/**
  * Supervisor which starts and stop the computation of a matching
  * @param pb an IAProblem
  * @param restricted true if only subgroups of size -1 are investigated
  * @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  * */
class InclusiveSupervisor(pb: IAProblem, rule: SocialRule) extends Actor{
  val debug=false

  var solver : ActorRef = _
  var individualAgents = Seq[ActorRef]()//References to the agents representing individuals
  var activityAgents = Seq[ActorRef]()//References to the agents representing activities
  var nbFree=pb.m//How many individuals have finished
  var addressesActivityAgents= Map[String, ActorRef]()//White page for the activity agents
  var addressesIndividualAgents= Map[String, ActorRef]()//White page for the individual agents

  val matching = new  Matching(pb)// The matching to build

  /**
    * Method invoked after starting the actor
    */
  override def preStart(): Unit = {
    //Start the activity agents
    pb.activities.foreach { case a: Activity =>
      val actor = context.actorOf(Props(classOf[InclusiveCoalitionAgent], a, rule), a.name)
      activityAgents :+= actor
      addressesActivityAgents += (a.name -> actor)
    }//Start the individual agents
     pb.individuals.foreach{ case i : Individual =>
       val actor =  context.actorOf(Props(classOf[IndividualAgent], i), i.name)
       individualAgents :+= actor
       addressesIndividualAgents += (i.name -> actor)
    }
    //Send them the white pages
    individualAgents.foreach(actor => actor ! Inform(addressesActivityAgents))
  }

  /**
    * Method invoked when a message is received
    */
  def receive = {
    //When the works should be done
    case Start =>
      solver = sender
      //Signal all the individuals that they should start and run
      individualAgents.foreach(_ !  Start)

    //When an assignement is undone
    case Disassignement(i) =>
      nbFree+= 1
      matching.a+= (pb.getIndividual(i) -> Activity.VOID)
      if (debug) println("Supervisor: "+i+" is disassigned ("+nbFree+")")
      sender ! Confirm

    // When an assignement is done
    case Assignement(i,a) =>
      nbFree-= 1
      if (debug) println("Supervisor: "+i+" is assigned ("+nbFree+")")
      matching.a+= (pb.getIndividual(i) -> pb.getActivity(a))
      //If all the individual are assigned the build the matching
      if (nbFree == 0){
        pb.activities.foreach{ case a : Activity =>
          val g= matching.p(a)
          pb.individuals.foreach { case i : Individual =>
              if (matching.a(i)==a) matching.g+= (i -> g)
          }
        }
        solver ! Result(matching)// report the allocation
        individualAgents.foreach(i => i ! Stop)
        activityAgents.foreach(a => a ! Stop)
        context.stop(self)//stop the supervisor
      }

    case msg@_ => println(s"Supervisor receives a message which was not expected: $msg")
  }
}
