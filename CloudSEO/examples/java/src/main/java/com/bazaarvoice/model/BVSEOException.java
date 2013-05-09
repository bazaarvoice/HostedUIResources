package com.bazaarvoice.model;

public class BVSEOException
        extends RuntimeException
{
    public BVSEOException(String message) {
        super(message);
    }

    public BVSEOException(String message, Throwable cause) {
        super(message, cause);
    }
}
