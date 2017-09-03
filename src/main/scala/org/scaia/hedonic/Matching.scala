// Copyright (C) Maxime MORGE 2017
package org.scaia.hedonic

/**
  * Hedonic game partitioning the players
  * @constructor Create a hedonic game matching
  * @param g The hedonic game
  * @param partition The set of set of players
  */
class Matching(var g: Game, var partition: Partition){

  /**
    * Debugging the properties checking
    */
  val debug = false

  /**
    * Returns a string describing the matching
    * @return
    */
  override def toString: String = partition.toString


  /**
    * Return true if obj is a matching which is equals to this
    * @param obj Any object
    */
  override def equals(obj: scala.Any): Boolean = obj match {
    case that: Matching => {
      this.partition.equals(that.partition)
    }
    case _ => false
  }


  /**
    * Returns true of the partition contains all the players and all the intersection of two coalitions is empty
    */
  def isSoundMatching(): Boolean ={
    g.players.foreach{ p=>
      var nbSet=0
      partition.foreach{ c =>
        if (c.contains(p)) nbSet+=1
      }
      if (nbSet!=1) {
        if (debug) println(p+" is present "+nbSet+" times in "+partition)
        return false
      }
    }
    true
  }

  /**
    *   Return the coalition for a player
    * @param p the player
    */
  def coalitionFor(p: Player) : Coalition = {
    partition.foreach{ c =>
      if (c.contains(p)) return c
    }
    throw new RuntimeException("coalitionFor: this is not a matching")
  }

  /**
    * Returns true if the matching is rational
    */
  def isRational() : Boolean={
    g.players.foreach{ p =>
      val c= coalitionFor(p)
      if (!p.isRational(c.names)){
        if (debug) println(coalitionFor(p)+" is not individually rational for "+p)
        return false
      }
    }
    true
  }

  /**
    *  Returns true if the matching is blocked by a coalition c
    * @param c The names of the players in the coalition
    */
  def isWeaklyBlocked(c: Set[String]) : Boolean ={
    c.foreach{ name=>
      val p= g.getPlayer(name)
      if (! p.spref(c,coalitionFor(p).names)) return false
    }
    return true
  }

  /**
    * Returns true if the matching is core-stable
    */
  def isCoreStable() : Boolean ={
    g.nonIrrationalCoalitions().foreach{ c =>
      if (this.isWeaklyBlocked(c)) {
        if (debug) println("CS: blocked by "+c)
        return false
      }
    }
    true
  }

  /**
    * Returns true if the matching is Nash stable
    */
  def isNashStable() : Boolean ={
    g.players.foreach{ p => // foreach players p
      val pC=coalitionFor(p) // pC is the coalition for p
    val otherCoalitions =partition.filterNot(_.equals(pC))+new Coalition()
      if (debug) println("Nash stable: "+p+" in "+pC+" considers "+otherCoalitions)
      otherCoalitions.foreach{ oC => // for each other potential coalition for p oC
        val nCnames= oC.names + p.name
        if (p.spref(nCnames,pC.names)) { // if p strictly prefers oC to pC
          if (debug) println("NS: "+p+" strictly prefers join "+oC+" to "+pC)
          return false
        }else {
          if (debug) println("NS: "+p+" does not prefers join "+oC+" to "+pC)
        }
      }
    }
    true
  }

  /**
    *   Returns true if the matching is individually stable
    */
  def isIndividuallyStable() : Boolean ={
    g.players.foreach { p =>
      val pC = coalitionFor(p) // pC is the coalition for p
    val otherCoalitions = partition.filterNot(_.equals(pC)) + new Coalition()
      otherCoalitions.foreach { oC => // for each other potential coalition for p oC
        val nCnames = oC.names + p.name
        if (p.spref(nCnames, pC.names)) { // if p strictly prefers oC to pC
          if (oC.forall(o => o.pref(nCnames, oC.names))) {
            if (debug) println("IS: " + p + " strictly prefers join " + oC + " to " + pC)
            return false
          }
        }
      }
    }
    true
  }

  /**
    * Returns true if the matching is contractually individually stable
    */
  def isContractuallyIndividuallyStable() : Boolean = {
    g.players.foreach { p =>
      val pC = coalitionFor(p) // pC is the colaition for p
    val otherCoalitions = partition.filterNot(_.equals(pC)) + new Coalition()
      otherCoalitions.foreach { oC => // for each other potential coalition for p oC
        val nCnames = oC.names + p.name
        if (p.spref(nCnames, pC.names)) {
          val sC = pC - p
          if (oC.forall(o => o.pref(nCnames, oC.names)) && sC.forall(s => s.pref(sC.names, pC.names))) {
            if (debug) println("CIS: " + p + " strictly prefers join " + oC + " to " + pC)
            return false
          }
        }
      }
    }
    true
  }


  /**
    *   TODO Return true if the matching is contractually strict core stable
    */
  def isContractuallyStrictCore(): Boolean = {
    true
  }


}
