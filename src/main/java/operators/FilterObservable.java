package operators;

import core.Disposable;
import core.Observable;
import core.Observer;
import java.util.function.Predicate;

/**
 * FilterObservable — оператор для фильтрации элементов исходного Observable.
 * Пропускает только те элементы, для которых predicate возвращает true.
 *
 * @param <T> Тип элементов потока
 */
public class FilterObservable<T> extends Observable<T> {
    // Исходный Observable, на который будет подписка
    private final Observable<T> source;
    // Предикат, определяющий, должен ли элемент быть пропущен дальше
    private final Predicate<T> predicate;

    /**
     * Конструктор FilterObservable.
     *
     * @param source исходный Observable
     * @param predicate функция-фильтр, возвращающая true для элементов, которые должны быть пропущены
     */
    public FilterObservable(Observable<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    /**
     * Подписка на исходный Observable.
     * Каждый элемент проверяется предикатом: если predicate возвращает true,
     * элемент передается дальше подписчику; иначе игнорируется.
     * Ошибки и завершение пробрасываются вниз по цепочке.
     *
     * @param observer подписчик, который получит отфильтрованные элементы
     * @return Disposable для управления подпиской
     */
    @Override
    public Disposable subscribe(Observer<? super T> observer) {
        // Подписываемся на исходный Observable, фильтруем элементы через предикат
        return source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                try {
                    // Если элемент проходит фильтр, передаем его дальше
                    if (predicate.test(item)) {
                        observer.onNext(item);
                    }
                } catch (Throwable t) {
                    // Если предикат выбрасывает исключение, передаем ошибку подписчику
                    observer.onError(t);
                }
            }

            @Override
            public void onError(Throwable t) {
                // Пробрасываем ошибку дальше
                observer.onError(t);
            }

            @Override
            public void onComplete() {
                // Сообщаем о завершении потока
                observer.onComplete();
            }
        });
    }
}
