package de.woq.cats.effect

import cats.effect._
import cats.implicits._
import cats.effect.concurrent.Semaphore
import java.io._

object FileCopy:

  def copy(origin : File, destination : File)(using concurrent : Concurrent[IO]) : IO[Long] = for {
    guard <- Semaphore[IO](1)
    count <- inputOutputStreams(origin, destination, guard).use { case (in, out) =>
      guard.withPermit(transfer(in, out))
    }
  } yield count
  end copy

  private def transfer(origin : InputStream, destination : OutputStream) : IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10))
      total  <- transmit(origin, destination, buffer, 0L)
    } yield total
  end transfer

  private def transmit(origin : InputStream, destination : OutputStream, buffer: Array[Byte], acc: Long) : IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.size))
      count  <- if (amount > -1) IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
                else IO.pure(acc)
    } yield count
  end transmit

  private def inputStream(f : File, guard : Semaphore[IO]) : Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f))
    } { in =>
      IO(in.close()).handleErrorWith(_ => IO.unit)
    }
  end inputStream

  private def outputStream(f : File, guard : Semaphore[IO]) : Resource[IO, FileOutputStream] =
    Resource.make{
      IO(new FileOutputStream(f))
    } { out =>
      IO(out.close()).handleErrorWith(_ => IO.unit)
    }
  end outputStream

  private def inputOutputStreams(in : File, out: File, guard : Semaphore[IO]) : Resource[IO, (FileInputStream, FileOutputStream)] =
    for {
      in <- inputStream(in, guard)
      out <- outputStream(out, guard)
    } yield(in, out)
  end inputOutputStreams

end FileCopy

object Main extends IOApp:

  import FileCopy.copy

  override def run(args : List[String]) : IO[ExitCode] =
    for {
      _    <- if(args.length < 2) IO.raiseError(new IllegalArgumentException("Need origin and destination file name."))
              else IO.unit
      orig =  new File(args(0))
      dest =  new File(args(1))
      cnt  <- FileCopy.copy(orig, dest)
      _    <- IO(println(s"[$cnt] bytes copied from [$orig] to [$dest]"))
    } yield ExitCode.Success
  end run

end Main
