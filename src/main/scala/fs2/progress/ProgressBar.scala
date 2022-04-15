package fs2.progress

import cats.effect.IO
import me.tongfei.progressbar.{ProgressBar => pBar}
import fs2.{Stream, Pipe}
import cats.syntax.all._
import cats.effect.kernel.Resource
import cats.effect.kernel.Sync
import me.tongfei.progressbar.ProgressBarBuilder

trait ProgressBar[F[_]] {

  /**
   * Pauses this current progress.
   */
  def pause: F[Unit]

  /**
   * Prompts the progress bar to refresh. Normally a user should not call this function.
   */
  def refresh: F[Unit]

  /** Resets the progress bar to its initial state (where progress equals to 0). */
  def reset: F[Unit]

  /**
   * Resumes this current progress.
   */
  def resume: F[Unit]

  /**
   * Sets the extra message at the end of the progress bar.
   * @param msg New message
   */
  def setExtraMessage(msg: String): F[Unit]

  /**
   * Returns the current progress.
   */
  def current: F[Long]

  /**
   * Returns the extra message at the end of the progress bar.
   */
  def extraMessage: F[String]

  /**
   * Returns the maximum value of this progress bar.
   */
  def max: F[Long]

  /**
   * Returns the name of this task.
   */
  def taskName: F[String]

  /**
   * Gives a hint to the maximum value of the progress bar.
   * @param n Hint of the maximum value. A value of -1 indicates that the progress bar is indefinite.
   */
  def maxHint(n: Long): F[Unit]

  /**
   * Advances this progress bar by one step.
   */
  def step: F[Unit]

  /**
   * Advances this progress bar by a specific amount.
   * @param n Step size
   */
  def stepBy(n: Long): F[Unit]

  /**
   * Advances this progress bar to the specific progress value.
   * @param n New progress value
   */
  def stepTo(n: Long): F[Unit]

  /**
    * Increments the progress bar with the index of the stream element currently being processed.
    */
  def pipe[A]: Pipe[F, A, A]
  
}

object ProgressBar {
  /**
   * Creates a Resource yielding a progress bar with the specific taskName name and initial maximum value.
   * @param task Task name
   * @param initialMax Initial maximum value
   */
  def resource[F[_] : Sync](name: String, initialMax: Long): Resource[F, ProgressBar[F]] = Resource.eval(Sync[F].delay(
    new ProgressBarBuilder().setTaskName(name).setInitialMax(initialMax)
  )).flatMap(resource(_))

  /**
   * Creates a Resource yielding a progress bar reflecting the supplied builder.
   * @param builder ProgressBarBuilder representing the desired configuration
   */
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

  /**
   * Creates a Stream yielding a progress bar with the specific taskName name and initial maximum value.
   * @param task Task name
   * @param initialMax Initial maximum value
   */
  def stream[F[_] : Sync](name: String, initialMax: Long): Stream[F, ProgressBar[F]] = {
    Stream.resource(resource[F](name, initialMax))
  }

}