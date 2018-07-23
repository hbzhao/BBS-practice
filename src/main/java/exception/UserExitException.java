package exception;

public class UserExitException extends Exception
{
    public UserExitException(String errorMsg)
    {
        super(errorMsg);
    }
}
