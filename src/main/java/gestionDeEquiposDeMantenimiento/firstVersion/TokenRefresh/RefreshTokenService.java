package gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh;


import gestionDeEquiposDeMantenimiento.firstVersion.Exceptions.RefreshTokenExpiredException;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;


    @Transactional
    public RefreshTokenModel crearRefreshToken(UserModel user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush();

        RefreshTokenModel refreshToken = new RefreshTokenModel();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshTokenModel> SearchByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshTokenModel verifyExpiration(RefreshTokenModel token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expirado, inicia de nuevo sesión");

        }
        return token;
    }

    @Transactional
    public void deleteByUser(UserModel user) {
        refreshTokenRepository.deleteByUser(user);
    }


}
