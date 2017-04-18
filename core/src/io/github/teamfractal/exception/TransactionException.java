/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 */

package io.github.teamfractal.exception;

public class TransactionException extends RuntimeException {
    public TransactionException() {
        super();
    }

    public TransactionException(String message) {
        super(message);
    }
}
