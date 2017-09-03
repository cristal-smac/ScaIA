// Copyright (C) Maxime MORGE 2016
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia.IAProblem
import org.scaia.solver._
import org.scaia.solver.asia.{DistributedMNSolver, Utilitarian}
/**
 * Main app to compute a random example
 * */
object TestSolverWithRandomPb{
	val system = ActorSystem("ScaIA")//The Actor system
	def main(args: Array[String]): Unit = {
			val pb = IAProblem.generateRandom(2,3)
					println(pb.describe)

					val allMatchings = pb.allSoundMatchings()
					val allStableMatchings= allMatchings.filter(_.isStrictCoreStable())
					var bestUtilitarian= - Double.MaxValue
					var bestEgalitarian= - Double.MaxValue

					println("All stable matchings: ")
					allStableMatchings.foreach{m =>
					val u=m.utilitarianWelfare()
					val e=m.egalitarianWelfare()
					println(m)
					println("Utilitarian welfare "+u)
					println("Egalitarian welfare "+e)
					if (u>bestUtilitarian) bestUtilitarian=u
					if (e>bestEgalitarian) bestEgalitarian=e
			}
			println("Best utilitarian welfare "+bestUtilitarian)
			println("Best egalitarian welfare "+bestEgalitarian)

			val solver = new DistributedMNSolver(pb,system,false,Utilitarian)
			val result= solver.solve()
			println(result)
			println("Result is stable ? "+result.isStrictCoreStable())
			println("Utilitarian welfare "+result.utilitarianWelfare())
			println("Egalitarian welfare "+result.egalitarianWelfare())
	}
}