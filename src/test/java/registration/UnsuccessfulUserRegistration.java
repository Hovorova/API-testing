package registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnsuccessfulUserRegistration extends Registration {
    private String error;
}
