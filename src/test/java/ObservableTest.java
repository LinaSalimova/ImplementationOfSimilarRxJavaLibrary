import core.Observable;
import core.Observer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ObservableTest {
    @Test
    public void testMapAndFilter() {
        StringBuilder result = new StringBuilder();
        Observable.just("a", "bb", "ccc")
                .map(String::toUpperCase)
                .filter(s -> s.length() > 1)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String item) { result.append(item); }
                    @Override
                    public void onError(Throwable t) { fail(); }
                    @Override
                    public void onComplete() {}
                });
        assertEquals("BBCCC", result.toString());
    }
}

