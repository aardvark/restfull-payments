package ru.latuhin.revolut.payments.rest.endpoint;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Ideable;
import ru.latuhin.revolut.payments.rest.endpoint.dao.storage.Storage;

class StorageImpl<I extends Id, T extends Ideable<I>> implements Storage<T, I> {

  private NavigableMap<I, T> storage = new ConcurrentSkipListMap<>();

  @Override
  public T save(T object) {
    storage.put(object.getId(), object);
    return object;
  }

  @Override
  public T findById(I id) {
    return storage.get(id);
  }

  @Override
  public I reserveId() {
    if (storage.isEmpty()) {
      return null;
    }
    I id = storage.lastKey();
    return id.next();
  }

  @Override
  public boolean exist(I id) {
    return storage.containsKey(id);
  }
}
