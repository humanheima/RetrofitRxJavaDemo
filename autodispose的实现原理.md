```java
public static <T> AutoDisposeConverter<T> autoDisposable(final CompletableSource scope) {
        checkNotNull(scope, "scope == null");
        return new AutoDisposeConverter<T>() {

            @Override
            public CompletableSubscribeProxy apply(final Completable upstream) {
                if (!AutoDisposePlugins.hideProxies) {
                    return new AutoDisposeCompletable(upstream, scope);
                }
                return new CompletableSubscribeProxy() {
                    @Override
                    public Disposable subscribe() {
                        return new AutoDisposeCompletable(upstream, scope).subscribe();
                    }

                    @Override
                    public Disposable subscribe(Action action) {
                        return new AutoDisposeCompletable(upstream, scope).subscribe(action);
                    }

                    @Override
                    public Disposable subscribe(Action action, Consumer<? super Throwable> onError) {
                        return new AutoDisposeCompletable(upstream, scope).subscribe(action, onError);
                    }

                    @Override
                    public void subscribe(CompletableObserver observer) {
                        new AutoDisposeCompletable(upstream, scope).subscribe(observer);
                    }

                    @Override
                    public <E extends CompletableObserver> E subscribeWith(E observer) {
                        return new AutoDisposeCompletable(upstream, scope).subscribeWith(observer);
                    }

                    @Override
                    public TestObserver<Void> test() {
                        TestObserver<Void> observer = new TestObserver<>();
                        subscribe(observer);
                        return observer;
                    }

                    @Override
                    public TestObserver<Void> test(boolean cancel) {
                        TestObserver<Void> observer = new TestObserver<>();
                        if (cancel) {
                            observer.cancel();
                        }
                        subscribe(observer);
                        return observer;
                    }
                };
            }

            @Override
            public ObservableSubscribeProxy<T> apply(final Observable<T> upstream) {
                if (!AutoDisposePlugins.hideProxies) {
                    return new AutoDisposeObservable<>(upstream, scope);
                }
                return new ObservableSubscribeProxy<T>() {
                    @Override
                    public Disposable subscribe() {
                        return new AutoDisposeObservable<>(upstream, scope).subscribe();
                    }

                    @Override
                    public Disposable subscribe(Consumer<? super T> onNext) {
                        return new AutoDisposeObservable<>(upstream, scope).subscribe(onNext);
                    }

                    @Override
                    public Disposable subscribe(
                            Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
                        return new AutoDisposeObservable<>(upstream, scope).subscribe(onNext, onError);
                    }

                    @Override
                    public Disposable subscribe(
                            Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
                        return new AutoDisposeObservable<>(upstream, scope)
                                .subscribe(onNext, onError, onComplete);
                    }

                    @Override
                    public Disposable subscribe(
                            Consumer<? super T> onNext,
                            Consumer<? super Throwable> onError,
                            Action onComplete,
                            Consumer<? super Disposable> onSubscribe) {
                        return new AutoDisposeObservable<>(upstream, scope)
                                .subscribe(onNext, onError, onComplete, onSubscribe);
                    }

                    @Override
                    public void subscribe(Observer<? super T> observer) {
                        new AutoDisposeObservable<>(upstream, scope).subscribe(observer);
                    }

                    @Override
                    public <E extends Observer<? super T>> E subscribeWith(E observer) {
                        return new AutoDisposeObservable<>(upstream, scope).subscribeWith(observer);
                    }

                    @Override
                    public TestObserver<T> test() {
                        TestObserver<T> observer = new TestObserver<>();
                        subscribe(observer);
                        return observer;
                    }

                    @Override
                    public TestObserver<T> test(boolean dispose) {
                        TestObserver<T> observer = new TestObserver<>();
                        if (dispose) {
                            observer.dispose();
                        }
                        subscribe(observer);
                        return observer;
                    }
                };
            }

        };
    }
```
```
public static <T> AutoDisposeConverter<T> autoDisposable(final CompletableSource scope) {
        
    return new AutoDisposeConverter<T>() {
            
        //...

        @Override
        public ObservableSubscribeProxy<T> apply(final Observable<T> upstream) {
                
            if (!AutoDisposePlugins.hideProxies) {
                return new AutoDisposeObservable<>(upstream, scope);
            }
            return new ObservableSubscribeProxy<T>() {
                    

                //...
                    
                @Override
                public void subscribe(Observer<? super T> observer) {
                    new AutoDisposeObservable<>(upstream, scope).subscribe(observer);
                }
                   
            };
        }
    };
}
```