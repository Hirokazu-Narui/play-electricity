package net.svil.bootcamp.electricity.Models
class Fee(val baseCharge: Long, val usageCharge:Long)

object Calculator{
  def accumelate[P <: Plan](h: History, plan: P, currentLimit: Int): Fee = {
    plan match {
      case FlatRatePlan(name, flatRate) => new Fee(0L, h.data.foldLeft(0.0){ case (a, (k, v)) => a+v*4*flatRate}.toLong)
      case FlatRateWithCurrentLimitBaseCharge(name, flatRate, base) => {
        new Fee(base(currentLimit).toLong, h.data.foldLeft(0.0){ case (a, (k, v)) => a+v*4*flatRate}.toLong)
      }
      case StageRateWithCurrentLimitBaseCharge(name, stageTotalF, base) => {
        val total:Long = h.data.foldLeft(0L){case (a, (k,v)) => a+v*4}
        new Fee(base(currentLimit).toLong, (stageTotalF(total)).toLong)
      }
      case DayNightWithCurrentLimitBaseCharge(name, hourRate, base) => {
        val totalPayAsYouGo:Long = h.data.foldLeft(0L){
          case (a, (k,v)) => {
                  a+ (hourRate(k.getHour())*v*4).toLong
          }
        }
        new Fee(base(currentLimit).toLong, totalPayAsYouGo)
      }
      case _ => new Fee(0L, 0L)
    }
  }
}

object Solver{
  def solve(h: History, planCollection: Seq[Plan]): Seq[(String, Long)] = {
    var pairs : Seq[(String,Long)] = Seq()
    for (plan <- planCollection){
      pairs = pairs :+ ((plan.name, Calculator.accumelate(h, plan, 20).baseCharge + Calculator.accumelate(h, plan, 20).usageCharge/1000))
    }
    pairs.sortBy(_._2)
  }
}
