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
  *  Distributed implementation of an heuristics which returns a "good" matching with additive increasing and positive preferences
  *  @param pb to solve
  *  @param system of Actors
  *  @param restricted true if only subgroups of size -1 are investigated
  *  @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  *
  */
class DistributedInclusiveSolver(pb : IAProblem, system: ActorSystem, restricted: Boolean, rule: SocialRule) extends ASIASolver(pb){

  val TIMEOUTVALUE=100 seconds// default timeout of a run
  implicit val timeout = Timeout(TIMEOUTVALUE)

  override def solve() : Matching = {
    // Launch a new supervisor
    DistributedMNSolver.id+=1
    val supervisor = system.actorOf(Props(classOf[InclusiveSupervisor], pb, restricted, rule), name = "supervisor"+DistributedMNSolver.id)
    // The current thread is blocked and it waits for the supervisor to "complete" the Future with it's reply.
    val future = supervisor ? Start
    val result = Await.result(future, timeout.duration).asInstanceOf[Result]
    result.matching
  }
}


