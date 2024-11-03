package meelesh.authorizationService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import meelesh.authorizationService.dto.DTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeleteUserResponseDTO extends DTO {

    private boolean isDeleted;

}
