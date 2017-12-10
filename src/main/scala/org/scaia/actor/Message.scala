// Copyright (C) Maxime MORGE 2017
package org.scaia.actor

import org.scaia.asia.Matching

import akka.actor.ActorRef

/**
  *  All possible messages between the actors
  */
class Message
case object Start extends Message// Start the solver
case class Result(matching: Matching) extends  Message// The solver returns a matching
case class Inform(addresses :  Map[String, ActorRef]) extends Message// Send a white page
case object Stop extends Message//Stop an agent

case object Confirm extends Message// Confirm the exclusion
case class Assignement(i: String, a: String) extends Message// The assignement of i with a is reported
case class Disassignement(i: String) extends Message// The disassignement of i is reported

case class Propose(i: String) extends Message//i makes a proposal
case object Accept extends  Message// Accept a proposal
case object Reject extends Message// Reject a proposal
case object Withdraw extends Message// Fire an individual

case class Query(g: Set[String], a: String) extends Message// Ask for an utility to be in g for a
case class Reply(g: Set[String], a: String, utility: Double) extends Message// Reply with the utility to be in g for a
