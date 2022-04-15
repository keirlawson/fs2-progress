package fs2.progress

import cats.effect.IO
import me.tongfei.progressbar.{ProgressBar => pBar}
import fs2.{Stream, Pipe}
import cats.implicits._
import cats.effect.kernel.Resource
import cats.effect.kernel.Sync

trait ProgressBar[F[_]] {

  def stepTo(n: Long): F[Unit]

  def pipe[A]: Pipe[F, A, A]
  
}

object ProgressBar {
  def resource[F[_] : Sync](name: String, initialMax: Long): Resource[F, ProgressBar[F]] = Resource.fromAutoCloseable(Sync[F].delay(new pBar(name, initialMax))).map { pb =>
    new ProgressBar[F] {

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