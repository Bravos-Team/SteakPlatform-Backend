env "local" {

  src = "file://atlas/schema.pg.hcl"

  url = "postgres://[username]:[password]@localhost:5432/steakdb?sslmode=disable"

  dev = "postgres://[username]:[password]@localhost:5432/difftest?sslmode=disable"

  migration {
    dir = "file://atlas/migrations"
  }

}
