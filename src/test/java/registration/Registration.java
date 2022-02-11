package registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Registration {
    private String email;
    private String password;

    public Registration() {
        super();
    }

    public Registration(String email,
                    String password) {
        this.email = email;
        this.password = password;
    }
}
