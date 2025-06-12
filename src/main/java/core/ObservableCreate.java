package core;

/**
 * ObservableCreate — конкретная реализация Observable, создающая поток данных "с нуля".
 * Используется для реализации метода Observable.create().
 *
 * @param <T> Тип элементов потока
 */
public class ObservableCreate<T> extends Observable<T> {
    // Источник событий, реализующий OnSubscribe (функциональный интерфейс)
    private final OnSubscribe<T> source;

    /**
     * Конструктор принимает источник событий (OnSubscribe).
     * @param source источник событий, определяющий логику эмиссии элементов
     */
    public ObservableCreate(OnSubscribe<T> source) {
        this.source = source;
    }

    /**
     * Подписывает observer на поток данных.
     * Создаёт emitter, который реализует интерфейс Emitter и Disposable.
     * Передаёт emitter источнику (source), чтобы тот мог эмитировать события.
     * @param observer подписчик, который получит элементы, ошибки и завершение
     * @return Disposable для управления подпиской
     */
    @Override
    public Disposable subscribe(Observer<? super T> observer) {
        CreateEmitter<T> emitter = new CreateEmitter<>(observer);
        // Передаём emitter источнику, чтобы он мог эмитировать события
        source.subscribe(emitter);
        return emitter;
    }

    /**
     * CreateEmitter — реализация Emitter и Disposable.
     * Позволяет источнику эмитировать события и управлять состоянием подписки.
     */
    static class CreateEmitter<T> implements Emitter<T> {
        private final Observer<? super T> observer;
        // Флаг отмены подписки
        private volatile boolean disposed = false;
        // Флаг завершения потока (onError или onComplete уже были вызваны)
        private volatile boolean done = false;

        /**
         * Конструктор принимает observer, которому будут передаваться события.
         * @param observer подписчик
         */
        CreateEmitter(Observer<? super T> observer) {
            this.observer = observer;
        }

        /**
         * Эмитирует новый элемент, если поток не завершён и подписка не отменена.
         * @param item элемент потока
         */
        @Override
        public void onNext(T item) {
            if (!done && !disposed) observer.onNext(item);
        }

        /**
         * Эмитирует ошибку, если поток не завершён и подписка не отменена.
         * После этого никаких событий больше не будет.
         * @param t ошибка
         */
        @Override
        public void onError(Throwable t) {
            if (!done && !disposed) {
                done = true;
                observer.onError(t);
            }
        }

        /**
         * Сообщает о завершении потока, если он ещё не завершён и подписка не отменена.
         * После этого никаких событий больше не будет.
         */
        @Override
        public void onComplete() {
            if (!done && !disposed) {
                done = true;
                observer.onComplete();
            }
        }

        /**
         * Отменяет подписку (больше не будут приниматься события).
         */
        @Override
        public void dispose() {
            disposed = true;
        }

        /**
         * Проверяет, отменена ли подписка.
         * @return true, если подписка отменена
         */
        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}
