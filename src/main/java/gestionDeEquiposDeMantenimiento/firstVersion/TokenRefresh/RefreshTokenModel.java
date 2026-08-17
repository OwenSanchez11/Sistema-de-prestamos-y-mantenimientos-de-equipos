package gestionDeEquiposDeMantenimiento.firstVersion.TokenRefresh;

import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="refresh_token")
@Getter
@Setter
public class RefreshTokenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "idUsuario")
    private UserModel user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

}
