package de.woq.cats.core

trait Printable[A]:
  def format(v : A) : String
end Printable

object PrintableInstances:

  given stringPrintable as Printable[String]:
    def format(v : String) = v

  given intPrintable as Printable[Int]:
    def format(v: Int) = v.toString

end PrintableInstances

object Printable:
  def format[A](v : A)(using p : Printable[A]) : String = p.format(v)
  def print[A](v : A)(using p : Printable[A]) : Unit = println(p.format(v))
end Printable

final case class Cat(name : String, age : Int, color : String)

object PrintableCat:

  import PrintableInstances.{given _}

  given catPrintable as Printable[Cat]:
    def format(v : Cat) : String =
      val name: String = Printable.format(v.name)
      val age: String = Printable.format(v.age)
      val color: String = Printable.format(v.color)
      s"$name is a $age year-old $color cat."
    end format
  end catPrintable
end PrintableCat

object PrintableSyntax:
  extension[A](v : A)(using p : Printable[A]):
    def format : String = p.format(v)
    def print : Unit = println(p.format(v))
end PrintableSyntax

@main def exercise_1_3() =
  import PrintableCat.{given _}
  import PrintableSyntax._
  Cat("Tigger", 10, "black").print
end exercise_1_3
