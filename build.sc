import mill._
import scalalib._

trait CatsModule extends ScalaModule {
  def scalaVersion = "0.26.0-RC1"

  override def ivyDeps : T[Agg[Dep]] = T { super.ivyDeps() ++ Agg(
    ivy"org.typelevel::cats-core:2.1.1".withDottyCompat(scalaVersion()),
    ivy"org.typelevel::cats-effect:2.1.4".withDottyCompat(scalaVersion())
  )}

  override def scalacOptions = Seq(
    "-deprecation",
    "-target:jvm-1.8",
    "-feature",
    "-unchecked",
    "-language:postfixOps,higherKinds,implicitConversions",
    "-migration"
  )
}

object catsSamples extends Module {

  object catsCore extends CatsModule {
    override def finalMainClass : T[String] = T { "de.woq.cats.core.exercise_1_3" }
  }

  object catsEffect extends CatsModule {
    override def finalMainClass : T[String] = T { "de.woq.cats.effect.Main"}
  }
}
