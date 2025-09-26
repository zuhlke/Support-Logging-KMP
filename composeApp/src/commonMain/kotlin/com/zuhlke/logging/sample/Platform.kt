package com.zuhlke.logging.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform