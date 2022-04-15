package fs2.progress

import cats.effect.IO
import me.tongfei.progressbar.{ProgressBar => pBar}
import fs2.{Stream, Pipe}
import cats.syntax.all._
import cats.effect.kernel.Resource
import cats.effect.kernel.Sync
import me.tongfei.progressbar.ProgressBarBuilder

trait ProgressBar[F[_]] {

  def pause: F[Unit]

  def refresh: F[Unit]

  def reset: F[Unit]

  def resume: F[Unit]

  def setExtraMessage(msg: String): F[Unit]

  def current: F[Long]

  def extraMessage: F[String]

  def max: F[Long]

  def taskName: F[String]

  def maxHint(n: Long): F[Unit]

  def step: F[Unit]

  def stepBy(n: Long): F[Unit]

  def stepTo(n: Long): F[Unit]

  def pipe[A]: Pipe[F, A, A]
  
}

object ProgressBar {
  def resource[F[_] : Sync](name: String, initialMax: Long): Resource[F, ProgressBar[F]] = Resource.eval(Sync[F].delay(
    new ProgressBarBuilder().setTaskName(name).setInitialMax(initialMax)
  )).flatMap(resource(_))

  def resource[F[_] : Sync](builder: ProgressBarBuilder): Resource[F, ProgressBar[F]] = Resource.fromAutoCloseable(Sync[F].delay(builder.build())).map { pb =>
    new ProgressBar[F] {

      override def pause: F[Unit] = Sync[F].delay(pb.pause())

      override def refresh: F[Unit] = Sync[F].delay(pb.refresh())

      override def reset: F[Unit] = Sync[F].delay(pb.reset())

      override def resume: F[Unit] = Sync[F].delay(pb.resume())

      override def setExtraMessage(msg: String): F[Unit] = Sync[F].delay(pb.setExtraMessage(msg))

      override def maxHint(n: Long): F[Unit] = Sync[F].delay(pb.maxHint(n))

      override def step: F[Unit] = Sync[F].delay(pb.step())

      override def stepBy(n: Long): F[Unit] = Sync[F].delay(pb.stepBy(n))


      override def current: F[Long] = Sync[F].delay(pb.getCurrent())

      override def extraMessage: F[String] = Sync[F].delay(pb.getExtraMessage())

      override def max: F[Long] = Sync[F].delay(pb.getMax())

      override def taskName: F[String] = Sync[F].delay(pb.getTaskName())

      override def pipe[A]: Pipe[F,A,A] = { input =>
        input.zipWithIndex.evalTap { case (_, idx) =>
          stepTo(idx + 1)
        }.map(_._1)
      }

      override def stepTo(n: Long): F[Unit] = Sync[F].delay(pb.stepTo(n))

    }
  }

  def stream[F[_] : Sync](name: String, initialMax: Long): Stream[F, ProgressBar[F]] = {
    Stream.resource(resource[F](name, initialMax))
  }

}