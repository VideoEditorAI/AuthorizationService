package meelesh.authorizationService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import meelesh.authorizationService.dto.DTO;
import meelesh.authorizationService.dto.TokenDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterResponseDTO extends DTO {

    private String id;
    private String username;
    private TokenDTO token;

}
