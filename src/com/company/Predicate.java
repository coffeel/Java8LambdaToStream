package com.company;

/**
 * Created by yonglinx on 7/28/16.
 */
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);

    default Predicate<T> and(Predicate<T> p1) {
        return t -> p1.test(t) && this.test(t);
    }

    default Predicate<T> or(Predicate<T> p1) {
        return t -> p1.test(t) || this.test(t);
    }

    static <U> Predicate<U> isEqualTo(U u) {
        return t -> t.equals(u);
    }
}
