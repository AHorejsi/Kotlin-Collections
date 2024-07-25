package collections

interface SortedMap<TKey, out TValue> : Map<TKey, TValue> {
    fun first(): Map.Entry<TKey, TValue>

    fun last(): Map.Entry<TKey, TValue>

    fun lesser(key: TKey, inclusive: Boolean): Map.Entry<TKey, TValue>

    fun greater(key: TKey, inclusive: Boolean): Map.Entry<TKey, TValue>
}

interface MutableSortedMap<TKey, TValue> : SortedMap<TKey, TValue>, MutableMap<TKey, TValue> {
    fun removeFirst(): MutableMap.MutableEntry<TKey, TValue>

    fun removeLast(): MutableMap.MutableEntry<TKey, TValue>
}

fun <TKey, TValue> SortedMap<TKey, TValue>.firstOrNull(): Map.Entry<TKey, TValue>? =
    if (this.isEmpty())
        null
    else
        this.first()

fun <TKey, TValue> SortedMap<TKey, TValue>.tryFirst(): Result<Map.Entry<TKey, TValue>> =
    runCatching { this.first() }

fun <TKey> SortedMap<TKey, *>.firstKey(): TKey = this.first().key

fun <TKey> SortedMap<TKey, *>.firstKeyOrNull(): TKey? = this.firstOrNull()?.key

fun <TKey> SortedMap<TKey, *>.tryFirstKey(): Result<TKey> = this.tryFirst().map(Map.Entry<TKey, *>::key)

fun <TValue> SortedMap<*, TValue>.firstValue(): TValue = this.first().value

fun <TValue> SortedMap<*, TValue>.firstValueOrNull(): TValue? = this.firstOrNull()?.value

fun <TValue> SortedMap<*, TValue>.tryFirstValue(): Result<TValue> = this.tryFirst().map(Map.Entry<*, TValue>::value)

fun <TKey, TValue> SortedMap<TKey, TValue>.lastOrNull(): Map.Entry<TKey, TValue>? =
    if (this.isEmpty())
        null
    else
        this.last()

fun <TKey, TValue> SortedMap<TKey, TValue>.tryLast(): Result<Map.Entry<TKey, TValue>> =
    runCatching { this.last() }

fun <TKey> SortedMap<TKey, *>.lastKey(): TKey = this.last().key

fun <TKey> SortedMap<TKey, *>.lastKeyOrNull(): TKey? = this.lastOrNull()?.key

fun <TKey> SortedMap<TKey, *>.tryLastKey(): Result<TKey> = this.tryLast().map(Map.Entry<TKey, *>::key)

fun <TValue> SortedMap<*, TValue>.lastValue(): TValue = this.last().value

fun <TValue> SortedMap<*, TValue>.lastValueOrNull(): TValue? = this.lastOrNull()?.value

fun <TValue> SortedMap<*, TValue>.tryLastValue(): Result<TValue> = this.tryLast().map(Map.Entry<*, TValue>::value)

fun <TKey, TValue> SortedMap<TKey, TValue>.lesserOrNull(key: TKey, inclusive: Boolean): Map.Entry<TKey, TValue>? =
    this.tryLesser(key, inclusive).getOrNull()

fun <TKey, TValue> SortedMap<TKey, TValue>.tryLesser(key: TKey, inclusive: Boolean): Result<Map.Entry<TKey, TValue>> =
    runCatching { this.lesser(key, inclusive) }

fun <TKey> SortedMap<TKey, *>.lesserKey(key: TKey, inclusive: Boolean): TKey =
    this.lesser(key, inclusive).key

fun <TKey> SortedMap<TKey, *>.lesserKeyOrNull(key: TKey, inclusive: Boolean): TKey? =
    this.lesserOrNull(key, inclusive)?.key

fun <TKey> SortedMap<TKey, *>.tryLesserKey(key: TKey, inclusive: Boolean): Result<TKey> =
    this.tryLesser(key, inclusive).map(Map.Entry<TKey, *>::key)

fun <TKey, TValue> SortedMap<TKey, TValue>.lesserValue(key: TKey, inclusive: Boolean): TValue =
    this.lesser(key, inclusive).value

fun <TKey, TValue> SortedMap<TKey, TValue>.lesserValueOrNull(key: TKey, inclusive: Boolean): TValue? =
    this.lesserOrNull(key, inclusive)?.value

fun <TKey, TValue> SortedMap<TKey, TValue>.tryLesserValue(key: TKey, inclusive: Boolean): Result<TValue> =
    this.tryLesser(key, inclusive).map(Map.Entry<*, TValue>::value)

fun <TKey, TValue> SortedMap<TKey, TValue>.greaterOrNull(key: TKey, inclusive: Boolean): Map.Entry<TKey, TValue>? =
    this.tryGreater(key, inclusive).getOrNull()

fun <TKey, TValue> SortedMap<TKey, TValue>.tryGreater(key: TKey, inclusive: Boolean): Result<Map.Entry<TKey, TValue>> =
    runCatching { this.greater(key, inclusive) }

fun <TKey> SortedMap<TKey, *>.greaterKey(key: TKey, inclusive: Boolean): TKey =
    this.greater(key, inclusive).key

fun <TKey> SortedMap<TKey, *>.greaterKeyOrNull(key: TKey, inclusive: Boolean): TKey? =
    this.greaterOrNull(key, inclusive)?.key

fun <TKey> SortedMap<TKey, *>.tryGreaterKey(key: TKey, inclusive: Boolean): Result<TKey> =
    this.tryGreater(key, inclusive).map(Map.Entry<TKey, *>::key)

fun <TKey, TValue> SortedMap<TKey, TValue>.greaterValue(key: TKey, inclusive: Boolean): TValue =
    this.greater(key, inclusive).value

fun <TKey, TValue> SortedMap<TKey, TValue>.greaterValueOrNull(key: TKey, inclusive: Boolean): TValue? =
    this.greaterOrNull(key, inclusive)?.value

fun <TKey, TValue> SortedMap<TKey, TValue>.tryGreaterValue(key: TKey, inclusive: Boolean): Result<TValue> =
    this.tryGreater(key, inclusive).map(Map.Entry<*, TValue>::value)

fun <TKey, TValue> MutableSortedMap<TKey, TValue>.removeFirstOrNull(): MutableMap.MutableEntry<TKey, TValue>? =
    if (this.isEmpty())
        null
    else
        this.removeFirst()

fun <TKey, TValue> MutableSortedMap<TKey, TValue>.tryRemoveFirst(): Result<MutableMap.MutableEntry<TKey, TValue>> =
    runCatching { this.removeFirst() }

fun <TKey, TValue> MutableSortedMap<TKey, TValue>.removeLastOrNull(): MutableMap.MutableEntry<TKey, TValue>? =
    if (this.isEmpty())
        null
    else
        this.removeLast()

fun <TKey, TValue> MutableSortedMap<TKey, TValue>.tryRemoveLast(): Result<MutableMap.MutableEntry<TKey, TValue>> =
    runCatching { this.removeLast() }
