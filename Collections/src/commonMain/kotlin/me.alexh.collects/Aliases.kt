package me.alexh.collects

typealias Predicate<TElement> = (TElement) -> Boolean
typealias Factory<TElement> = () -> TElement
typealias Transformer<TInput, TOutput> = (TInput) -> TOutput
typealias LeftAccumulator<TInput, TOutput> = (TOutput, TInput) -> TOutput
typealias RightAccumulator<TInput, TOutput> = (TInput, TOutput) -> TOutput
