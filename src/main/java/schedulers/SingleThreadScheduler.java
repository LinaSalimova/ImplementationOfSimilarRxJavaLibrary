package schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SingleThreadScheduler — реализация Scheduler,
 * которая выполняет все задачи последовательно в одном выделенном потоке.
 * Используется для сценариев, где важен порядок выполнения (например, обновление UI).
 */
public class SingleThreadScheduler implements Scheduler {
    // ExecutorService с одним потоком: все задачи выполняются по очереди в одном и том же потоке
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Планирует выполнение задачи (Runnable) в выделенном потоке.
     * @param task задача, которую нужно выполнить
     */
    @Override
    public void execute(Runnable task) {
        executor.submit(task); // Передаём задачу в очередь ExecutorService
    }
}
