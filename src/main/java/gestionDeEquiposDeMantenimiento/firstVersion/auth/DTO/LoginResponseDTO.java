
package gestionDeEquiposDeMantenimiento.firstVersion.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta que se le da al usuario al autenticar el login")
public class LoginResponseDTO {
    private String message;
    private Boolean authenticated;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
