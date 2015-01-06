package exceptions;

/**
 * Created by apaliy on 1/6/2015.
 */
public class OurRuntimeException extends RuntimeException {
    public OurRuntimeException() {
        super();
    }

    public OurRuntimeException(String s) {
        super(s);
    }

    public OurRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public OurRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
