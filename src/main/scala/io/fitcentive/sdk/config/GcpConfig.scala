package io.fitcentive.sdk.config

import com.google.auth.Credentials

case class GcpConfig(credentials: Credentials, project: String)
