package schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * IOThreadScheduler — реализация Scheduler для асинхронных IO-операций.
 * Использует CachedThreadPool, который динамически создает новые потоки по мере необходимости
 * и переиспользует уже завершённые потоки для выполнения новых задач.
 * Такой Scheduler подходит для задач, связанных с вводом-выводом, сетевыми запросами, файловыми операциями и т.п.
 */
public class IOThreadScheduler implements Scheduler {
    // ExecutorService с CachedThreadPool: количество потоков ограничено только ресурсами системы,
    // потоки создаются и уничтожаются автоматически
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Планирует выполнение задачи (Runnable) в пуле потоков для IO-операций.
     * @param task задача, которую нужно выполнить
     */
    @Override
    public void execute(Runnable task) {
        executor.submit(task); // Передаём задачу в ExecutorService
    }
}
