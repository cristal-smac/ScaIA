// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.scaia.actor.{InclusiveSupervisor, Result, Start}
import org.scaia.asia.{IAProblem, Matching}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  *  Distributed implementation of an heuristics which returns a "good" matching
  *  @param pb to solve
  *  @param system of Actors
  *  @param approximation true if only subgroups of size-1 are investigated
  *  @param rule to apply (maximize the utilitarian/egalitarian welfare
  *
  */
class DistributedInclusiveSolver(pb : IAProblem, system: ActorSystem, rule: SocialRule) extends ASIASolver(pb){

  val TIMEOUTVALUE=100 seconds// default timeout of a run
  implicit val timeout = Timeout(TIMEOUTVALUE)
  DistributedInclusiveSolver.id+=1

  override def solve() : Matching = {
    // Launch a new supervisor
    DistributedInclusiveSolver.id+=1
    if (debug) system.eventStream.setLogLevel(akka.event.Logging.DebugLevel)
    val supervisor = system.actorOf(Props(classOf[InclusiveSupervisor], pb, rule), name = "supervisor"+DistributedInclusiveSolver.id)
    // The current thread is blocked and it waits for the supervisor to "complete" the Future with it's reply.
    val future = supervisor ? Start
    val result = Await.result(future, timeout.duration).asInstanceOf[Result]
    result.matching
  }
}

object DistributedInclusiveSolver{
  var id = 0
}

