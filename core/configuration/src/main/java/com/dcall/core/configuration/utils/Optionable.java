package com.dcall.core.configuration.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Optionable<T> {
    private static final Optionable<?> EMPTY = new Optionable<>();
    private final T value;
    private Optionable<Object> callback = empty();

    private Optionable() {
        this.value = null;
    }

    @SuppressWarnings("unchecked")
    public static<T> Optionable<T> empty() {
        return (Optionable<T>) EMPTY;
    }

    private Optionable(T value) {
        this.value = Objects.requireNonNull(value);
    }

    public static <T> Optionable<T> of(T value) {
        return new Optionable<>(value);
    }

    public static <T> Optionable<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }


    @Deprecated
    public boolean isPresent() {
        return value != null;
    }

    @Deprecated
    public boolean isNotPresent() {
        return value == null;
    }

    public boolean isNotEmpty() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public Optionable getCallback() {
        if (callback == null) {
            throw new NoSuchElementException("No callback present");
        }
        return callback;
    }

    // executors
    public Optionable<T> consumeIfPresent(Consumer<? super T> action) {
        if (isNotEmpty()) {
            action.accept(value);
        }
        return this;
    }


    public <U> Optionable consumeIfPresent(Optionable<U> next, Consumer<? super U> consumer) {
        if (isNotEmpty() && next.isNotEmpty()) {
            next.consumeIfPresent(consumer);
            return next;
        }

       return next;
    }

    public <U> Optionable<T> applyIfPresent(Function<? super T, ? super U> action) {
        if (isNotEmpty()) {
            callback = Optionable.ofNullable(action.apply(value));
        }

        return this;
    }

    public <U, V> Optionable applyIfPresent(Optionable<U> next, Function<? super U, ? super V> function) {
        if (isNotEmpty() && next.isNotEmpty()) {
            next.applyIfPresent(function);
            return next;
        }

        return next;
    }

    // operators
    public <U> Optionable or(Optionable<U> next, Consumer<? super Object> consumer) {
        if (isNotEmpty()) {
            consumer.accept(value);
            return this;
        }
        next.consumeIfPresent(consumer);
        return next;
    }

    public <U, V> Optionable or(Optionable<U> next, Function<? super Object, V> consumer) {
        if (isNotEmpty()) {
            callback = Optionable.of(consumer.apply(value));
            return this;
        }
        next.applyIfPresent(consumer);
        return next;
    }

//    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
//        if (value != null) {
//            action.accept(value);
//        } else {
//            emptyAction.run();
//        }
//    }
//
//    public Optional<T> filter(Predicate<? super T> predicate) {
//        Objects.requireNonNull(predicate);
//        if (!isPresent()) {
//            return this;
//        } else {
//            return predicate.test(value) ? this : empty();
//        }
//    }
//
//    /**
//     * If a value is present, returns an {@code Optional} describing (as if by
//     * {@link #ofNullable}) the result of applying the given mapping function to
//     * the value, otherwise returns an empty {@code Optional}.
//     *
//     * <p>If the mapping function returns a {@code null} result then this method
//     * returns an empty {@code Optional}.
//     *
//     * @apiNote
//     * This method supports post-processing on {@code Optional} values, without
//     * the need to explicitly check for a return status.  For example, the
//     * following code traverses a stream of URIs, selects one that has not
//     * yet been processed, and creates a path from that URI, returning
//     * an {@code Optional<Path>}:
//     *
//     * <pre>{@code
//     *     Optional<Path> p =
//     *         uris.stream().filter(uri -> !isProcessedYet(uri))
//     *                       .findFirst()
//     *                       .map(Paths::get);
//     * }</pre>
//     *
//     * Here, {@code findFirst} returns an {@code Optional<URI>}, and then
//     * {@code map} returns an {@code Optional<Path>} for the desired
//     * URI if one exists.
//     *
//     * @param mapper the mapping function to apply to a value, if present
//     * @param <U> The type of the value returned from the mapping function
//     * @return an {@code Optional} describing the result of applying a mapping
//     *         function to the value of this {@code Optional}, if a value is
//     *         present, otherwise an empty {@code Optional}
//     * @throws NullPointerException if the mapping function is {@code null}
//     */
//    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
//        Objects.requireNonNull(mapper);
//        if (!isPresent()) {
//            return empty();
//        } else {
//            return Optional.ofNullable(mapper.apply(value));
//        }
//    }
//
//    /**
//     * If a value is present, returns the result of applying the given
//     * {@code Optional}-bearing mapping function to the value, otherwise returns
//     * an empty {@code Optional}.
//     *
//     * <p>This method is similar to {@link #map(Function)}, but the mapping
//     * function is one whose result is already an {@code Optional}, and if
//     * invoked, {@code flatMap} does not wrap it within an additional
//     * {@code Optional}.
//     *
//     * @param <U> The type of value of the {@code Optional} returned by the
//     *            mapping function
//     * @param mapper the mapping function to apply to a value, if present
//     * @return the result of applying an {@code Optional}-bearing mapping
//     *         function to the value of this {@code Optional}, if a value is
//     *         present, otherwise an empty {@code Optional}
//     * @throws NullPointerException if the mapping function is {@code null} or
//     *         returns a {@code null} result
//     */
//    public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
//        Objects.requireNonNull(mapper);
//        if (!isPresent()) {
//            return empty();
//        } else {
//            @SuppressWarnings("unchecked")
//            Optional<U> r = (Optional<U>) mapper.apply(value);
//            return Objects.requireNonNull(r);
//        }
//    }
//
//    /**
//     * If a value is present, returns an {@code Optional} describing the value,
//     * otherwise returns an {@code Optional} produced by the supplying function.
//     *
//     * @param supplier the supplying function that produces an {@code Optional}
//     *        to be returned
//     * @return returns an {@code Optional} describing the value of this
//     *         {@code Optional}, if a value is present, otherwise an
//     *         {@code Optional} produced by the supplying function.
//     * @throws NullPointerException if the supplying function is {@code null} or
//     *         produces a {@code null} result
//     * @since 9
//     */
//    public Optional<T> or(Supplier<? extends Optional<? extends T>> supplier) {
//        Objects.requireNonNull(supplier);
//        if (isPresent()) {
//            return this;
//        } else {
//            @SuppressWarnings("unchecked")
//            Optional<T> r = (Optional<T>) supplier.get();
//            return Objects.requireNonNull(r);
//        }
//    }
//
//    /**
//     * If a value is present, returns a sequential {@link Stream} containing
//     * only that value, otherwise returns an empty {@code Stream}.
//     *
//     * @apiNote
//     * This method can be used to transform a {@code Stream} of optional
//     * elements to a {@code Stream} of present value elements:
//     * <pre>{@code
//     *     Stream<Optional<T>> os = ..
//     *     Stream<T> s = os.flatMap(Optional::stream)
//     * }</pre>
//     *
//     * @return the optional value as a {@code Stream}
//     * @since 9
//     */
//    public Stream<T> stream() {
//        if (!isPresent()) {
//            return Stream.empty();
//        } else {
//            return Stream.of(value);
//        }
//    }
//
//    /**
//     * If a value is present, returns the value, otherwise returns
//     * {@code other}.
//     *
//     * @param other the value to be returned, if no value is present.
//     *        May be {@code null}.
//     * @return the value, if present, otherwise {@code other}
//     */
//    public T orElse(T other) {
//        return value != null ? value : other;
//    }
//
//    /**
//     * If a value is present, returns the value, otherwise returns the result
//     * produced by the supplying function.
//     *
//     * @param supplier the supplying function that produces a value to be returned
//     * @return the value, if present, otherwise the result produced by the
//     *         supplying function
//     * @throws NullPointerException if no value is present and the supplying
//     *         function is {@code null}
//     */
//    public T orElseGet(Supplier<? extends T> supplier) {
//        return value != null ? value : supplier.get();
//    }
//
//    /**
//     * If a value is present, returns the value, otherwise throws an exception
//     * produced by the exception supplying function.
//     *
//     * @apiNote
//     * A method reference to the exception constructor with an empty argument
//     * list can be used as the supplier. For example,
//     * {@code IllegalStateException::new}
//     *
//     * @param <X> Type of the exception to be thrown
//     * @param exceptionSupplier the supplying function that produces an
//     *        exception to be thrown
//     * @return the value, if present
//     * @throws X if no value is present
//     * @throws NullPointerException if no value is present and the exception
//     *          supplying function is {@code null}
//     */
//    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
//        if (value != null) {
//            return value;
//        } else {
//            throw exceptionSupplier.get();
//        }
//    }
//
//    /**
//     * Indicates whether some other object is "equal to" this {@code Optional}.
//     * The other object is considered equal if:
//     * <ul>
//     * <li>it is also an {@code Optional} and;
//     * <li>both instances have no value present or;
//     * <li>the present values are "equal to" each other via {@code equals()}.
//     * </ul>
//     *
//     * @param obj an object to be tested for equality
//     * @return {@code true} if the other object is "equal to" this object
//     *         otherwise {@code false}
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//
//        if (!(obj instanceof Optional)) {
//            return false;
//        }
//
//        Optional<?> other = (Optional<?>) obj;
//        return Objects.equals(value, other.value);
//    }
//
//    /**
//     * Returns the hash code of the value, if present, otherwise {@code 0}
//     * (zero) if no value is present.
//     *
//     * @return hash code value of the present value or {@code 0} if no value is
//     *         present
//     */
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(value);
//    }
//
//    @Override
//    public String toString() {
//        return value != null
//                ? String.format("Optional[%s]", value)
//                : "Optional.empty";
//    }
}
