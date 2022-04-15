# fs2-progress
A command line progress bar that you can feed your streams to. Published for Scala 2.13 and 3.1.

Wraps [Progressbar](https://tongfei.me/progressbar/).

## Installation

Add the following to your SBT library dependencies:

```
"io.github.keirlawson" %% "fs2-progress" % "0.2.0"
```

## Usage

An example that tracks the progress in emitting the letters of the alphabet:

```scala
import fs2.Stream
import scala.concurrent.duration._
import cats.effect.IO

val elements = 'A' to 'Z'
val letters = Stream.emits[IO, Char](elements).metered(500.millis) //emit one letter every 500 millis

ProgressBar.stream[IO]("Letters progress", elements.length).flatMap { progress =>
    letters.through(progress.pipe)
}
```