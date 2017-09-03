package asia

import asia.ToyExample._
import org.scaia.asia.{Group, Matching}
import org.scaia.solver.asia.{MNSolver, Utilitarian}
import org.scalatest.FlatSpec

class TestToyProblem extends FlatSpec{

  val m= new Matching(pb)
  m.a= Map(philippe -> simu, jeanchristophe -> dps, maxime-> dps, antoine -> simu)
  m.g= Map(maxime -> Group(maxime, philippe), antoine -> Group(philippe, antoine), philippe -> Group(philippe, antoine), jeanchristophe -> Group(jeanchristophe, maxime))


  "The matching M with pM(simu)={philippe,antoine} and pM(dps)={maxime, jeanchristophe}" should "be stable" in {
    assert(m.isStrictCoreStable())
  }

  "The matching M with pM(simu)={philippe,antoine} and pM(dps)={maxime, jeanchristophe}" should "be Pareto-optimal" in {
    assert(m.isParetoOptimal())
  }

  "The maximum utilitarian matching" should "be M with pM(simu)={philippe,antoine} and pM(dps)={maxime, jeanchristophe}" in {
    var u = -Double.MaxValue
    var maxUtilMatching = new Matching(pb)
    pb.allSoundMatchings().foreach { m =>
      if (m.utilitarianWelfare() >= u) {
        u = m.utilitarianWelfare()
        maxUtilMatching = m
      }
    }
    assert(m.equals(maxUtilMatching))
  }


  "The MNSolver" should "return M with pM(simu)={philippe,antoine} and pM(dps)={maxime, jeanchristophe}" in {
    val solver= new MNSolver(pb, false, Utilitarian)
    val resultR = solver.solve()
    assert(m.equals(resultR))
  }

}
