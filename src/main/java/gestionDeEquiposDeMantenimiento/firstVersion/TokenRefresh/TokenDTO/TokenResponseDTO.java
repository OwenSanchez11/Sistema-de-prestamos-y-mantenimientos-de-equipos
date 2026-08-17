package gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh.TokenDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDTO {
    String newAccessToken;
    String newRefreshToke;

}
