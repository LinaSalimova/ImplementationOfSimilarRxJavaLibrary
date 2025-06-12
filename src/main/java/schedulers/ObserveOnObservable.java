package schedulers;

import core.Disposable;
import core.Observable;
import core.Observer;

/**
 * ObserveOnObservable — оператор-обёртка для Observable, реализующий observeOn.
 * Позволяет переключить поток, в котором вызываются методы onNext, onError, onComplete у Observer.
 * Это важно для управления тем, где (в каком Scheduler/потоке) будет происходить обработка событий подписчиком.
 *
 * @param <T> тип элементов в потоке
 */
public class ObserveOnObservable<T> extends Observable<T> {
    // Исходный Observable, к которому применяется оператор observeOn
    private final Observable<T> source;
    // Scheduler, в котором будут вызываться методы Observer
    private final Scheduler scheduler;

    /**
     * Конструктор. Принимает исходный Observable и Scheduler для обработки событий.
     * @param source исходный Observable
     * @param scheduler планировщик (Scheduler), на котором будут вызываться методы Observer
     */
    public ObserveOnObservable(Observable<T> source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    /**
     * Подписывает observer на source, но все события (onNext, onError, onComplete)
     * будут проксированы в указанный Scheduler (например, в отдельный поток).
     *
     * @param observer наблюдатель, который получит события
     * @return Disposable для управления подпиской
     */
    @Override
    public Disposable subscribe(Observer<? super T> observer) {
        // Подписываемся на исходный Observable, но все события проксируем через Scheduler
        return source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                // onNext будет вызван в Scheduler (например, в другом потоке)
                scheduler.execute(() -> observer.onNext(item));
            }

            @Override
            public void onError(Throwable t) {
                // onError будет вызван в Scheduler
                scheduler.execute(() -> observer.onError(t));
            }

            @Override
            public void onComplete() {
                // onComplete будет вызван в Scheduler
                scheduler.execute(observer::onComplete);
            }
        });
    }
}
