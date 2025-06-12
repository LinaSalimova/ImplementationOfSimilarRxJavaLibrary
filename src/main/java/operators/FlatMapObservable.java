package operators;

import core.Disposable;
import core.Observable;
import core.Observer;
import java.util.function.Function;

/**
 * FlatMapObservable — оператор для "плоского" отображения (flatMap) элементов исходного Observable.
 * Каждый элемент исходного потока преобразуется функцией mapper в новый Observable<R>,
 * а затем все элементы этих внутренних Observable объединяются в один поток.
 *
 * @param <T> Тип исходных элементов
 * @param <R> Тип элементов внутреннего Observable
 */
public class FlatMapObservable<T, R> extends Observable<R> {
    // Исходный Observable, на который будет подписка
    private final Observable<T> source;
    // Функция, которая каждому элементу сопоставляет новый Observable<R>
    private final Function<T, Observable<R>> mapper;

    /**
     * Конструктор FlatMapObservable.
     *
     * @param source исходный Observable
     * @param mapper функция, отображающая элемент типа T в Observable<R>
     */
    public FlatMapObservable(Observable<T> source, Function<T, Observable<R>> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    /**
     * Подписка на исходный Observable.
     * Для каждого элемента вызывается mapper, который возвращает новый Observable<R>,
     * и на него немедленно подписывается новый Observer.
     * Все элементы из внутренних Observable<R> передаются основному observer'у.
     * Ошибки пробрасываются вниз по цепочке.
     *
     * @param observer подписчик, который получит элементы из всех внутренних Observable
     * @return Disposable для управления подпиской
     */
    @Override
    public Disposable subscribe(Observer<? super R> observer) {
        // Подписываемся на исходный Observable, для каждого элемента создаём новый Observable<R>
        return source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                try {
                    // Для каждого элемента исходного потока подписываемся на соответствующий Observable<R>
                    mapper.apply(item).subscribe(new Observer<R>() {
                        @Override
                        public void onNext(R r) {
                            // Каждый элемент внутреннего Observable передаём дальше
                            observer.onNext(r);
                        }
                        @Override
                        public void onError(Throwable t) {
                            // Ошибки из внутреннего Observable пробрасываем вниз
                            observer.onError(t);
                        }
                        @Override
                        public void onComplete() {
                            // Завершение внутреннего Observable игнорируем (общий onComplete вызовется ниже)
                        }
                    });
                } catch (Throwable t) {
                    // Если mapper выбрасывает исключение, пробрасываем ошибку вниз
                    observer.onError(t);
                }
            }
            @Override
            public void onError(Throwable t) {
                // Ошибки исходного Observable пробрасываем вниз
                observer.onError(t);
            }
            @Override
            public void onComplete() {
                // Завершение исходного Observable пробрасываем вниз
                observer.onComplete();
            }
        });
    }
}
