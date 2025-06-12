
import core.Observable;
import core.Observer;
import schedulers.IOThreadScheduler;
import schedulers.SingleThreadScheduler;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Создаем Observable с помощью create, эмитируем два элемента и завершаем поток
        Observable.create((Observable.Emitter<String> emitter) -> {
                    emitter.onNext("Hello");
                    emitter.onNext("World");
                    emitter.onComplete();
                })
                // Применяем оператор map: преобразуем строку к верхнему регистру
                .map(String::toUpperCase)
                // Применяем оператор filter: пропускаем только строки длиннее 4 символов
                .filter(s -> s.length() > 4)
                // Применяем оператор flatMap: к каждой строке добавляем "!!!" и упаковываем в новый Observable
                .flatMap(s -> Observable.just(s + "!!!"))
                // subscribeOn: подписка будет выполнена в IO-потоке
                .subscribeOn(new IOThreadScheduler())
                // observeOn: обработка элементов будет происходить в SingleThreadScheduler (один поток)
                .observeOn(new SingleThreadScheduler())
                // Подписываемся на поток данных
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String item) {
                        // Выводим каждый элемент, указывая имя потока
                        System.out.println(Thread.currentThread().getName() + " onNext: " + item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        // В случае ошибки выводим сообщение
                        System.err.println("onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        // По завершении потока выводим сообщение
                        System.out.println("onComplete");
                    }
                });

        // Ожидаем завершения асинхронных операций, чтобы поток main не завершился раньше времени
        Thread.sleep(1000);
    }
}
