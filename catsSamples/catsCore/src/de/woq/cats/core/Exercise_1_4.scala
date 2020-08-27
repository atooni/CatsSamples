package de.woq.cats.core

import cats._
import cats.implicits._

import java.util.Date
import java.text.SimpleDateFormat

given dateShow as Show[Date] = new Show[Date] {
  override def show(d : Date) : String =
    val sdf : SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS")
    sdf.format(d)
  }
end dateShow

given catShow as Show[Cat] = new Show[Cat]:
  override def show(c : Cat) : String =
    val name : String = c.name.show
    val age : String = c.age.show
    val color : String = c.color.show
    s"$name is a $age year-old $color cat."
end catShow

@main def exercise_1_4() =
  println(new Cat("Tigger", 10, "black").show)
