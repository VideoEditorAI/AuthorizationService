package meelesh.authorizationService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import meelesh.authorizationService.dto.DTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterRequestDTO extends DTO {

    private String email;
    private String username;
    private String password;

}
