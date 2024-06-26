package io.fitcentive.sdk.infrastructure.database

import anorm.{NamedParameter, RowParser, SQL}
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import play.api.db.Database

trait DatabaseClient {
  def db: Database
  def dbec: DatabaseExecutionContext

  def getRecords[A](sql: String, args: NamedParameter*)(implicit parser: RowParser[A]): Seq[A] =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).as(parser.*)
    }

  def getRecordOpt[A](sql: String, args: NamedParameter*)(implicit parser: RowParser[A]): Option[A] =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).as(parser.singleOpt)
    }

  def getRecord[A](sql: String, args: NamedParameter*)(implicit parser: RowParser[A]): A =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).as(parser.single)
    }

  def executeSqlWithExpectedReturn[A](sql: String, args: Seq[NamedParameter])(implicit parser: RowParser[A]): A =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).as(parser.single)
    }

  def executeSqlWithoutReturning(sql: String, args: Seq[NamedParameter]): Boolean =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).execute()
    }

  def insertRecord[A](sql: String, args: Seq[NamedParameter])(implicit parser: RowParser[A]): A =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).executeInsert(parser.single)
    }

  def insertRecords[A](sql: String, args: Seq[NamedParameter])(implicit parser: RowParser[A]): Seq[A] =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).executeInsert(parser.*)
    }

  def insertRecordsTransactional[A](
    sqlToArgs: Seq[(String, Seq[NamedParameter])]
  )(implicit parser: RowParser[A]): Seq[A] =
    db.withTransaction { implicit conn =>
      sqlToArgs.map {
        case (sql, args) =>
          SQL(sql).on(args: _*).executeInsert(parser.single)
      }
    }

  def deleteRecords(sql: String, args: NamedParameter*): Int =
    db.withConnection { implicit conn =>
      SQL(sql).on(args: _*).executeUpdate()
    }

  def deleteRecords(sqlToArgs: Seq[(String, Seq[NamedParameter])]): Seq[Int] =
    db.withTransaction { implicit conn =>
      sqlToArgs.map {
        case (sql, args) =>
          SQL(sql).on(args: _*).executeUpdate()
      }
    }
}
