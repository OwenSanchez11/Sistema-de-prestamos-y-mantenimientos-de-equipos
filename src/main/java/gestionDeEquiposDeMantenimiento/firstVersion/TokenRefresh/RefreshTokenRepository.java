package gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh;

import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, UUID> {
        Optional<RefreshTokenModel> findByToken(String token);
        @Transactional
        void deleteByUser(UserModel user);
        Optional<RefreshTokenModel> findByUser(UserModel user);
}
