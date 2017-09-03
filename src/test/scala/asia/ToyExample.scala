// Copyright (C) Maxime MORGE 2017
package asia

import org.scaia.asia._

/**
  * Toy example where
  * */
object ToyExample {
  val a = new Activity("a",2)
  val b = new Activity("b",2)

  val dps = new Activity("dps",2)
  val simu = new Activity("simu",2)
  // Philippe
  val philippe: Individual = new Individual("philippe",4) {
    vMap+=("simu" -> 1.0 , "dps" -> -1.0)
    wMap+=("jean-christophe" -> -1.0)
    wMap+=("antoine" -> 1.0)
    wMap+=("maxime" -> 1.0)
  }
  // Jean-Chrisophe
  val jeanchristophe: Individual = new Individual("jean-christophe",4) {
    vMap+=("simu" -> 0.5, "dps" -> 0.5)
    wMap+=("philippe" -> 0.5)
    wMap+=("maxime" -> 0.5)
    wMap+=("antoine" -> 0.5)
  }
  // Maxime
  val maxime: Individual = new Individual("maxime",4) {
    vMap+=("simu" -> -1.0 , "dps" -> 1.0)
    wMap+=("antoine" -> 0.0)
    wMap+=("philippe" -> 1.0)
    wMap+=("jean-christophe"-> 1.0)
  }
  // Antoine
  val antoine: Individual = new Individual("antoine",4) {//The utility of the individual with respect to activities
    vMap+=("simu" -> 0.5 , "dps" -> 0.5)
    wMap+=("maxime" -> -1.0)
    wMap+=("jean-christophe" -> -1.0)
    wMap+=("philippe" -> 1.0 )
  }

  val pb= new IAProblem(Group(philippe,maxime,jeanchristophe,antoine), Set(simu,dps))
  println(pb)

}