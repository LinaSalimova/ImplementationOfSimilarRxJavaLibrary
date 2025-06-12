package operators;

import core.Disposable;
import core.Observable;
import core.Observer;
import java.util.function.Function;

/**
 * MapObservable — оператор для преобразования элементов исходного Observable.
 * Каждый элемент преобразуется функцией mapper и передается дальше по цепочке.
 *
 * @param <T> Тип исходных элементов
 * @param <R> Тип элементов после преобразования
 */
public class MapObservable<T, R> extends Observable<R> {
    // Исходный Observable, на который будет подписка
    private final Observable<T> source;
    // Функция-преобразователь элементов
    private final Function<T, R> mapper;

    /**
     * Конструктор MapObservable.
     *
     * @param source исходный Observable
     * @param mapper функция преобразования элементов типа T в элементы типа R
     */
    public MapObservable(Observable<T> source, Function<T, R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    /**
     * Подписка на исходный Observable.
     * Каждый элемент преобразуется функцией mapper и передается observer'у.
     * Если функция mapper выбрасывает исключение, оно передается в onError.
     *
     * @param observer подписчик, который получит преобразованные элементы
     * @return Disposable для управления подпиской
     */
    @Override
    public Disposable subscribe(Observer<? super R> observer) {
        // Подписываемся на source, оборачиваем observer для преобразования элементов
        return source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                try {
                    // Преобразуем элемент и передаем дальше
                    observer.onNext(mapper.apply(item));
                } catch (Throwable t) {
                    // Если функция mapper выбрасывает исключение, передаем его в onError
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
