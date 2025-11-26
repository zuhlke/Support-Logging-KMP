package com.zuhlke.logging.viewer.ui.utils

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char

val dateTimeFormatter = DateTimeComponents.Format {
    day(); char('/'); monthNumber(); char('/'); year()
    chars(", ")
    hour(); char(':'); minute(); char(':'); second()
    offset(UtcOffset.Formats.FOUR_DIGITS)
}

val timeFormatter = DateTimeComponents.Format {
    hour(); char(':'); minute(); char(':'); second(); char('.'); secondFraction(3)
    offset(UtcOffset.Formats.FOUR_DIGITS)
}