package schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ComputationScheduler — реализация Scheduler для вычислительных задач (CPU-bound).
 * Использует FixedThreadPool, количество потоков равно числу доступных процессоров.
 * Такой Scheduler оптимален для параллельных вычислений, не связанных с блокирующими операциями.
 */
public class ComputationScheduler implements Scheduler {
    // ExecutorService с фиксированным числом потоков, равным количеству доступных процессоров.
    // Это позволяет эффективно использовать CPU для параллельных вычислений.
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    /**
     * Планирует выполнение задачи (Runnable) в пуле потоков для вычислений.
     * @param task задача, которую нужно выполнить
     */
    @Override
    public void execute(Runnable task) {
        executor.submit(task); // Передаём задачу в ExecutorService для асинхронного выполнения
    }
}
