package core;

import operators.FilterObservable;
import operators.FlatMapObservable;
import operators.MapObservable;
import schedulers.ObserveOnObservable;
import schedulers.Scheduler;
import schedulers.SubscribeOnObservable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Абстрактный класс Observable — основа реактивного потока.
 * Позволяет создавать источники данных, строить цепочки операторов и управлять подпиской.
 * Реализует паттерн "Наблюдатель" (Observer pattern).
 *
 * @param <T> Тип элементов потока
 */
public abstract class Observable<T> {

    /**
     * Создаёт Observable с помощью пользовательского источника событий (OnSubscribe).
     * Пример использования: Observable.create(emitter -> { ... })
     *
     * @param source источник событий (лямбда или анонимный класс)
     * @param <T> тип элементов потока
     * @return новый Observable
     */
    public static <T> Observable<T> create(OnSubscribe<T> source) {
        return new ObservableCreate<>(source);
    }

    /**
     * Создаёт Observable, который последовательно эмитирует переданные элементы и завершает поток.
     *
     * @param items элементы для эмиссии
     * @param <T> тип элементов
     * @return новый Observable
     */
    public static <T> Observable<T> just(T... items) {
        return create(emitter -> {
            for (T item : items) {
                emitter.onNext(item);
            }
            emitter.onComplete();
        });
    }

    /**
     * Абстрактный метод подписки на Observable.
     * Реализации должны вызывать методы observer.onNext, onError, onComplete.
     *
     * @param observer подписчик (наблюдатель)
     * @return Disposable для управления подпиской
     */
    public abstract Disposable subscribe(Observer<? super T> observer);

    /**
     * Оператор map — преобразует каждый элемент потока с помощью функции mapper.
     *
     * @param mapper функция преобразования элементов
     * @param <R> тип элементов после преобразования
     * @return новый Observable с преобразованными элементами
     */
    public <R> Observable<R> map(Function<T, R> mapper) {
        return new MapObservable<>(this, mapper);
    }

    /**
     * Оператор filter — пропускает только те элементы, для которых predicate возвращает true.
     *
     * @param predicate функция-фильтр
     * @return новый Observable с отфильтрованными элементами
     */
    public Observable<T> filter(Predicate<T> predicate) {
        return new FilterObservable<>(this, predicate);
    }

    /**
     * Оператор flatMap — отображает каждый элемент в новый Observable и объединяет все элементы в один поток.
     *
     * @param mapper функция, возвращающая Observable для каждого элемента
     * @param <R> тип элементов внутреннего Observable
     * @return новый Observable, объединяющий все внутренние Observable
     */
    public <R> Observable<R> flatMap(Function<T, Observable<R>> mapper) {
        return new FlatMapObservable<>(this, mapper);
    }

    /**
     * Оператор subscribeOn — выполняет подписку на Observable в заданном Scheduler (потоке/пуле потоков).
     *
     * @param scheduler планировщик для выполнения подписки
     * @return Observable, подписка на который будет выполнена в Scheduler
     */
    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new SubscribeOnObservable<>(this, scheduler);
    }

    /**
     * Оператор observeOn — переключает поток, в котором вызываются методы observer (onNext, onError, onComplete).
     *
     * @param scheduler планировщик для обработки событий
     * @return Observable, события которого будут обработаны в Scheduler
     */
    public Observable<T> observeOn(Scheduler scheduler) {
        return new ObserveOnObservable<>(this, scheduler);
    }

    /**
     * Функциональный интерфейс OnSubscribe — определяет, как Observable эмитирует элементы подписчику.
     * Обычно реализуется через лямбду.
     *
     * @param <T> тип элементов
     */
    public interface OnSubscribe<T> {
        void subscribe(Emitter<T> emitter);
    }

    /**
     * Интерфейс Emitter — позволяет источнику эмитировать элементы, ошибки и завершение.
     * Также реализует Disposable для управления подпиской (отмена).
     *
     * @param <T> тип элементов
     */
    public interface Emitter<T> extends Disposable {
        void onNext(T item);
        void onError(Throwable t);
        void onComplete();
    }
}
