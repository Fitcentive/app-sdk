package io.fitcentive.sdk.utils

import anorm.NamedParameter

import java.util.UUID

trait AnormOps extends StringOps {

  /**
    * Note - this can only be used for simple types
    *        complex types that require casting may not work as intended
    */
  def makeOptionalSqlUpdateParams[A](namedParameters: Seq[NamedParameter]): String =
    namedParameters
      .map { p =>
        val snakeName = camelToSnakeCase(p.name)
        s"$snakeName = case when {${p.name}} is null then u.$snakeName else {${p.name}} end"
      }
      .mkString(", ")

  /**
    * Required SQL statement upto opening parenthesis
    * For example, select * from table_name where id in (
    */
  def transformUuidsToSql(uuids: Seq[UUID], sql: String): String =
    uuids.foldLeft(sql)((acc, id) => acc + s"'$id'::uuid,").dropRight(1) + ") ;"

}
