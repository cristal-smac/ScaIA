// Copyright (C) Maxime MORGE 2017
package org.scaia.hedonic

import org.scaia.asia.{Activity, IAProblem}

/**
  * Hedonic coalition game
  * @constructor Create a hedonic coalition game
  * @param players The players
  */
class Game(val players: Set[Player]){

  /**
    * Returns a string fully describing the game
    */
  override def toString: String = {
    var s= "Players: \n"
    players.foreach{ p =>
      s+="player "+p+" ("+p.rlc+")\n"
    }
    return s
  }


  /**
    * Returns the set of coalitions which are rational for at least one player
    */
  def nonIrrationalCoalitions(): Set[Set[String]] ={
    players.foldLeft(Set[Set[String]]())( (acc,p) => acc++p.rationalCoalitions())
  }

  /**
    * Returns the set of coalitions  which are rational for each player
    */
  def rationalCoalitions(): Set[Set[String]] ={
    var coalitions =Set[Set[String]]()
    var first=true
    players.foreach{ p =>
      if (first) {
        coalitions = p.rationalCoalitions()
        first=false
      } else coalitions= coalitions.intersect(p.rationalCoalitions())
    }
    coalitions
  }

  /**
    *   Returns a player
    *   @param name the name of the player
    */
  def getPlayer(name: String): Player ={
    players.foreach{p=>
      if (p.name.equals(name)) return p
    }
    throw new RuntimeException("There is no player "+name)
  }

}

/**
  * Factory for a game from a [[org.scaia.asia.IAProblem]]
  *
  */
object Game{
  def apply(pb: IAProblem): Game = {
    var players= Set[Player]()
    val names=pb.individuals.names
    pb.activities.foreach{ a=>
      players+=Player(a,pb)// call the factory
    }
    pb.individuals.foreach{ i=>
      players+=Player(i,pb)// call the factory
    }
    new Game(players)
  }
}