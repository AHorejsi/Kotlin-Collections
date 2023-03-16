package com.alexh.collects

internal fun countUp(start: Int, count: Int): IntRange = start .. (start + count)

internal fun countDown(start: Int, count: Int): IntRange = (start - count) .. start
