package fittoring.application.business.exception;

public class ReservationNotCompletedException extends RuntimeException {

  public ReservationNotCompletedException(String message) {
    super(message);
  }
}
