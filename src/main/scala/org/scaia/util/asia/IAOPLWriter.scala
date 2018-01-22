// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import java.io._

import com.typesafe.config.ConfigFactory
import org.scaia.asia.{Activity, Group, IAProblem, Individual}

/**
  * Build a IAOPLWriter object from a text file
  * @param pathName the output filename
  * @param pb is a IAProblem
  */
class IAOPLWriter(pathName: String, pb : IAProblem){
  val file = new File(pathName)
  def write() : Unit = {
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(pb.toOPL)
    bw.close()
  }
}

/**
  * Test IAOPLWriter
  */
object IAOPLWriter extends App{
  val config = ConfigFactory.load()
  import org.scaia.util.asia.example.DilemmaPref._
  val writer=new IAOPLWriter(config.getString("path.scaia")+"/"+config.getString("path.input"),pb)
  writer.write()
}


