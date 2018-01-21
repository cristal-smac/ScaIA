package asia

import org.scaia.util.asia.NotBestUtil._
import org.scaia.asia.{Activity, Coalition, Group, Matching}
import org.scaia.solver.asia.{SelectiveSolver, Utilitarian}
import org.scalatest.FlatSpec

class TestNotBestUtil extends FlatSpec{

  val m1= new Matching(pb)
  m1.a= Map(i1 -> a, i2 -> b, i3 -> b)
  m1.g= Map(i1 -> Group(i1), i2 -> Group(i2, i3), i3 -> Group(i2, i3))

  val m2= new Matching(pb)
  m2.a= Map(i1 -> a, i2 -> a, i3 -> b)
  m2.g= Map(i1 -> Group(i1, i2), i2 -> Group(i1, i2), i3 -> Group(i3))

  "The maximum utilitarian matching" should "be M with pM1(a)={i1} and pM1(b)={i2,i3}" in {
    var u = -Double.MaxValue
    var maxUtilMatching = new Matching(pb)
    pb.allSoundMatchings().foreach { m =>
      if (m.utilitarianWelfare() >= u) {
        u = m.utilitarianWelfare()
        maxUtilMatching = m
      }
    }
    assert(m1.equals(maxUtilMatching))
  }

  "The SelectiveSolver" should "return M2 with pM2(a)={i1, i2} and pM2(b)={i3}" in {
    val solver= new SelectiveSolver(pb, false, Utilitarian)
    val resultR = solver.solve()
    assert(m2.equals(resultR))
  }

  "The SelectiveSolver" should "not return the maximum utilitarian matching" in {
    val solver= new SelectiveSolver(pb, false, Utilitarian)
    val resultR = solver.solve()
    assert(! m1.equals(resultR))
  }

}
