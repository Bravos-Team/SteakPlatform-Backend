env "local" {

  src = "file://schema.pg.hcl"

  url = "postgres://bravos:4gi5uKqCtmYRRqRyoJTmu9U@localhost:5432/steakdb?sslmode=disable"

  dev = "postgres://bravos:4gi5uKqCtmYRRqRyoJTmu9U@localhost:5432/steakdev?sslmode=disable"

  migration {
    dir = "file://migrations"
  }

}
