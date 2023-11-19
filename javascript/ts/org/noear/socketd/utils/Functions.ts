/**
 * @author noear
 * @since 2.0
 * @class
 */
export interface IoBiConsumer<T, U> {
    (t: T, u: U);
}

/**
 * @author noear
 * @since 2.0
 * @class
 */
export interface IoConsumer<T> {
    (t: T);
}

/**
 * 可运行
 *
 * @author noear
 * @since 2.0
 *
 * @class
 */
export interface RunnableEx {
    ();
}