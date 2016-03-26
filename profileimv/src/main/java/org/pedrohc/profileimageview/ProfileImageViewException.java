package org.pedrohc.profileimageview;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 *
 */
final class ProfileImageViewException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor
     */
    public ProfileImageViewException() {}

    /**
     * Constructor
     */
    public ProfileImageViewException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public ProfileImageViewException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public ProfileImageViewException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}