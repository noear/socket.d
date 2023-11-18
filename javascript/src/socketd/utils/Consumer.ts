export interface Consumer<T> {
    (t: T): void
}