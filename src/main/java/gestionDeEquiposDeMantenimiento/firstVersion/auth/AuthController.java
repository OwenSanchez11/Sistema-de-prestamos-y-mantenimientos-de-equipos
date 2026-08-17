
package gestionDeEquiposDeMantenimiento.firstVersion.auth;


import gestionDeEquiposDeMantenimiento.firstVersion.Exceptions.ErrorResponse;
import gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh.TokenDTO.RefreshTokenDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh.TokenDTO.TokenResponseDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.auth.DTO.LoginRequestDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.auth.DTO.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login de usuario")
public class AuthController {
    private final AuthService authService;
    
    
    @Operation(summary = "Loguear al usuario")
    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "200",
            description = "Usuario autenticado"
        ),

        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping(path = "/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return this.authService.authenticate(request);
    }
    
    @PostMapping(path = "/refresh")
    public TokenResponseDTO refresh(@RequestBody RefreshTokenDTO request) {
        return authService.refreshToken(request);
    }
    
}
