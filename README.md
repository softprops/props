# props

simple(r) access to java property files

> no everyone starts with a dsl'd config file

## features

- typeclassed type coercion
- propery currying via property namespaces

## usage

    object App extends Application {
      import props.Props._
      val prod = Props()(file("app.properties")).ns("production")
      val db = prod.ns("db")
      val connections = db[Int]("maxconnections").getOrElse(1000)
      val pool = Pool(connections) {
        Db(db[String]("host"), db[Int]("port"))
      }
    }


Doug Tangren (softprops) 2011