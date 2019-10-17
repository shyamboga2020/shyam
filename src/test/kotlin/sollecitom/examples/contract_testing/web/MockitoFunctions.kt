package sollecitom.examples.contract_testing.web

import org.mockito.Mockito

// Used to prevent a problem with nullability and vanilla Mockito + Kotlin.
internal fun <T> eq(obj: T): T = Mockito.eq<T>(obj)