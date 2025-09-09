package org.acme.chat.infraestructure.out.event;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

public class MultiEmitter<T> {
    private final BroadcastProcessor<T> processor;

    public MultiEmitter() {
        this.processor = BroadcastProcessor.create();
    }

    public void emit(T item) {
        processor.onNext(item);
    }

    public Multi<T> getMulti() {
        return processor;
    }
}