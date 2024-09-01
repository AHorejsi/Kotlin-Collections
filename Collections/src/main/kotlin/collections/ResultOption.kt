package collections

import arrow.core.Option
import arrow.core.Some
import arrow.core.None
import arrow.core.getOrElse

fun <TItem> Option<TItem>.getOrThrow(): TItem =
    this.getOrNull()!!

fun <TItem> Option<TItem>.getOrDefault(default: TItem): TItem =
    this.getOrElse{ default }

fun <TItem> Option<TItem>.getOrDefault(lazyDefault: () -> TItem): TItem =
    this.getOrElse{ lazyDefault() }

fun <TItem> Result<TItem>.toOption(): Option<TItem> =
    this.map{ Some(it) }.getOrDefault(None)


