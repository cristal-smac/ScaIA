// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.hedonic

import org.scaia.hedonic._

import scala.util.control.Breaks._

/**
  * Class to return a Contractually Individually Stable (CIS) partition
  */
class CISSolver(g: Game) extends HedonicSolver(g){

  /**
    * Returns a Contractually Individually Stable (CIS) partition
    */
  override def solve() : Matching = {
    //Compute the initial configuration where all the players are alone
    var part=new Partition()
    g.players.foreach(p => part+= new Coalition(p))
    var m=new Matching(g,part)
    //Move players as much as possible
    var move= false
    breakable {
      g.players.foreach { p => // Foreach player
        val c = m.coalitionFor(p)
        //It tries to move from its current coalition c
        val oc = part - c
        breakable {
          oc.foreach { d => // For each potential destination d eventually empty
            val fd = d + p// the destination in the future
            val fc = c - p// the current coalition in the future
            // if the player would like to move and this move is allowed by both the new and the old coalition.
            if (p.spref(fd.names, fc.names) && fd.forall(n => n.pref(fd.names, d.names)) && fc.forall(o => o.pref(fc.names, c.names))) {
              if (debug) println("CISSolver: " + p + " moves from" + c + " to " + d)
              part = part - c - d + fd
              if (!fc.isEmpty) part+=fc// Eventually the non-empty current coalition in the future
              m = new Matching(g, part)
              move = true
            }
            if (move) break
          }
          if (move) {
            move=false
            break
          }
        }
      }
    }
    return m
  }

}
