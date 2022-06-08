package io.fitcentive.sdk.utils

import anorm.NamedParameter

trait AnormOps extends StringOps {

  def makeParams[A](namedParameters: Seq[NamedParameter]): String =
    namedParameters
      .map { p =>
        val snakeName = camelToSnakeCase(p.name)
        s"$snakeName = case when {${p.name}} is null then u.$snakeName else {${p.name}} end"
      }
      .mkString(", ")

}
