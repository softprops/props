package props

trait Namespaced {
  def ns(name: String): PropsLike
}

trait Coerced[T] {
  def to(s: String): T
}

trait PropsLike extends Namespaced {
  def apply[T: Coerced](key: String): Option[T]
}

object Coerced {
  implicit object identity extends Coerced[String] {
    def to(s: String): String = s
  }
  implicit object int extends Coerced[Int] {
    def to(s: String): Int = s.toInt
  }
}

object Props {
  import Coerced._

  def resource(name: String) =
    stream(getClass().getResourceAsStream(name))

  def file(name: String) =
    stream(new java.io.FileInputStream(new java.io.File(name)))

  def stream(is: java.io.InputStream) = {
    val p = new java.util.Properties
    p.load(is)
    p
  }
}

case class Props(ctx: String = "", delim: String = ".")
                (f: => java.util.Properties)
extends PropsLike {
  private lazy val src = f

  private def key(name: String) = ctx match {
    case "" => name
    case more => Seq(ctx, name).mkString(delim)
  }

  def ns(name: String) = Props(key(name), delim)(src)

  def apply[T: Coerced](name: String): Option[T] =
    src.getProperty(key(name)) match {
      case null => None
      case value => Some(implicitly[Coerced[T]].to(value))
    }
}
