// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.scaia.actor.{Result, Start, SelectiveSupervisor}
import org.scaia.asia.{IAProblem, Matching}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  *  Distributed implementation of an heuristics which returns a "good" matching
  *  @param pb to solve
  *  @param system of Actors
  *  @param approximation true if only subgroups of size-1 are considered
  *  @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  *
  */
class DistributedSelectiveSolver(pb : IAProblem, system: ActorSystem, approximation: Boolean, rule: SocialRule) extends ASIASolver(pb){

  val TIMEOUTVALUE=100 seconds// default timeout of a run
  implicit val timeout = Timeout(TIMEOUTVALUE)

  override def solve() : Matching = {
    // Launch a new supervisor
    DistributedSelectiveSolver.id+=1
    val supervisor = system.actorOf(Props(classOf[SelectiveSupervisor], pb, approximation, rule), name = "supervisor"+DistributedSelectiveSolver.id)
    // The current thread is blocked and it waits for the supervisor to "complete" the Future with it's reply.
    val future = supervisor ? Start
    val result = Await.result(future, timeout.duration).asInstanceOf[Result]
    result.matching
  }
}

object DistributedSelectiveSolver{
  var id = 0
}
