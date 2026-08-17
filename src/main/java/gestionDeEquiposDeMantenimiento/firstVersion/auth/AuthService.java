
package gestionDeEquiposDeMantenimiento.firstVersion.auth;

import gestionDeEquiposDeMantenimiento.firstVersion.Exceptions.InvalidCredentialsException;
import gestionDeEquiposDeMantenimiento.firstVersion.Security.JwtService;
import gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh.RefreshTokenModel;
import gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh.RefreshTokenService;
import gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh.TokenDTO.RefreshTokenDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh.TokenDTO.TokenResponseDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.auth.DTO.LoginRequestDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.auth.DTO.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public LoginResponseDTO authenticate(LoginRequestDTO request) {
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Unauthorized credentiales"));

        String hashedPassword = user.getPassword();


        if (passwordEncoder.matches(request.getPassword(), hashedPassword)) {
            String token = jwtService.generateToken(user);
            RefreshTokenModel refreshToken = refreshTokenService.crearRefreshToken(user);
            System.out.println("REFRESH TOKEN" + refreshToken);
            return new LoginResponseDTO("User autenticado con éxito", true, token, refreshToken.getToken(),"Bearer");

        }
        throw new InvalidCredentialsException("Unauthorized");




    }

    public TokenResponseDTO refreshToken(RefreshTokenDTO request) {
        RefreshTokenModel refreshToken = refreshTokenService.SearchByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidCredentialsException("Refresh Token inválido"));

        refreshTokenService.verifyExpiration(refreshToken);

        UserModel user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);
        RefreshTokenModel newRefreshToken = refreshTokenService.crearRefreshToken(user);

        return new TokenResponseDTO(newAccessToken, newRefreshToken.getToken());
    }


}
