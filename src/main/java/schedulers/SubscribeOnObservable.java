package schedulers;

import core.Disposable;
import core.Observable;
import core.Observer;

/**
 * Observable-обёртка, реализующая оператор subscribeOn.
 * Позволяет запускать подписку (subscribe) в заданном Scheduler (потоке/пуле потоков).
 * Это обеспечивает асинхронность подписки и выполнение источника в нужном потоке.
 */
public class SubscribeOnObservable<T> extends Observable<T> {
    // Исходный Observable, к которому применяется оператор subscribeOn
    private final Observable<T> source;
    // Scheduler, в котором будет выполняться подписка
    private final Scheduler scheduler;

    /**
     * Конструктор. Принимает исходный Observable и Scheduler, в котором будет происходить подписка.
     * @param source исходный Observable
     * @param scheduler планировщик (Scheduler) для выполнения подписки
     */
    public SubscribeOnObservable(Observable<T> source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    /**
     * Подписывает observer на source в заданном Scheduler.
     * Сам subscribe возвращает Disposable-заглушку, потому что отмена подписи в этом простом примере
     * не управляет реальным состоянием асинхронной задачи.
     * @param observer наблюдатель, который получит события
     * @return Disposable для совместимости с API
     */
    @Override
    public Disposable subscribe(Observer<? super T> observer) {
        // Выполняем подписку в Scheduler (например, в другом потоке)
        scheduler.execute(() -> source.subscribe(observer));
        // Возвращаем Disposable-заглушку (отмена не реализована для асинхронной задачи)
        return new Disposable() {
            @Override
            public void dispose() {
                // Здесь можно реализовать отмену, если требуется
            }
            @Override
            public boolean isDisposed() {
                return false;
            }
        };
    }
}
