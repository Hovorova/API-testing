package registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuccessfulUserRegistration extends Registration {
    private Integer id;
    private String token;
}
