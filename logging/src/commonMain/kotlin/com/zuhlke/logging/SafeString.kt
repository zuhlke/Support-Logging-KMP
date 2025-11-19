package com.zuhlke.logging

import io.exoquery.terpal.Interpolator
import io.exoquery.terpal.InterpolatorFunction
import io.exoquery.terpal.Messages

/**
 * Represents a string where all interpolated variable values can be replaced with `<redacted>`,
 * unless the variable is explicitly wrapped with [public] or [hash] to reveal or hash its value.
 */
public class SafeString internal constructor(
    internal val parts: List<String>,
    internal val params: List<Any>
) // TODO: rename? What about PrivacyString? Ask iOS team

@InterpolatorFunction<SafeStringInterpolator>(SafeStringInterpolator::class)
/**
 * Creates a [SafeString] from a string literal. All interpolated variable values can be
 * redacted in logs unless explicitly marked otherwise. **If you pass an already evaluated
 * [String], redaction will not happen.** Use [public] and [hash] to mark variables
 * that can be revealed or hashed in logs.
 */
public fun safeString(
    @Suppress("unused") string: String
): SafeString = Messages.throwPluginNotExecuted()

internal object SafeStringInterpolator : Interpolator<Any, SafeString> {
    override fun interpolate(parts: () -> List<String>, params: () -> List<Any>): SafeString =
        SafeString(parts(), params())
}

internal data class PublicParameter(val arg: Any)
internal data class HashParameter(val arg: Any)

/**
 * Marks a parameter as safe to log in plain text.
 */
public fun public(value: Any): Any =
    PublicParameter(value) // We return Any here to hide internal types from the public API

/**
 * Marks a parameter to be hashed before logging.
 */
public fun hash(value: Any): Any = HashParameter(value)
