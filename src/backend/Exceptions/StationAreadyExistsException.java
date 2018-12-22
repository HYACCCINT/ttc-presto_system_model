package backend.Exceptions;

/** Exception thrown in the case of attempting to add repetitive stations */
public class StationAreadyExistsException extends Exception
{
    public StationAreadyExistsException(String message)
    {
        super(message);
    }
}
