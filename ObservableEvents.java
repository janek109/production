package tools;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.subjects.PublishSubject;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ObservableEvents<T> {

    private final PublishSubject<Object> subject;
    private final T eventsContract;

    public ObservableEvents(Class<T> eventsContract) {
        this.subject = PublishSubject.create();
        this.eventsContract = eventsContract.cast(Proxy.newProxyInstance(
                eventsContract.getClassLoader(),
                new Class[]{eventsContract},
                this::invoke
        ));
    }

    public Observable<Object> observeOn(Scheduler scheduler) {
        return subject.observeOn(scheduler);
    }

    public void close() {
        subject.onComplete();
    }

    public T eventsContract() {
        return eventsContract;
    }

    private Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args.length == 1) {
            subject.onNext(args[0]);
        }
        return null;
    }
}
