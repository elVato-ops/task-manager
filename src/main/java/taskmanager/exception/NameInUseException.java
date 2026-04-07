package taskmanager.exception;

import lombok.Getter;

@Getter
public class NameInUseException extends RuntimeException
{
    private final String userName;

    public NameInUseException(String userName)
    {
        super("User name " + userName + " already exists");

        this.userName = userName;
    }
}