package com.palo.util;

public final class Util {
    public interface CheckedSupplier<T>{
        T get() throws Exception;
    }


    public interface CheckedRunnable{
        void run() throws Exception;
    }

    public static <T> T wrapCheckedException(CheckedSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void wrapCheckedException(CheckedRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T wrapCheckedExceptionRabbit(CheckedSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

}
