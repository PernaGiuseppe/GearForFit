package giuseppeperna.GearForFit.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class NotValidException extends RuntimeException {

    private List<String> errorMessages;

    public NotValidException(List<String> errorMessages) {
        super("Errori di validazione trovati");
        this.errorMessages = errorMessages;
    }
}
