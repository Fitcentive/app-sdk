package io.fitcentive.sdk.utils

trait StringOps {
  def camelToSnakeCase(name: String): String =
    "[A-Z\\d]".r.replaceAllIn(
      name,
      { m =>
        "_" + m.group(0).toLowerCase()
      }
    )

  def snakeToCamelCase(name: String): String =
    "_([a-z\\d])".r.replaceAllIn(
      name,
      { m =>
        m.group(1).toUpperCase()
      }
    )
}
