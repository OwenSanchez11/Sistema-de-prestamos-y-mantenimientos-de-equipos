package gestionDeEquiposDeMantenimiento.firstVersion.integration;


import gestionDeEquiposDeMantenimiento.firstVersion.Rol.RolModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Rol.RolName;
import gestionDeEquiposDeMantenimiento.firstVersion.Rol.RolRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.util.TestDataFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest extends IntegrationTestBase{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Test
    void shouldGetAllUsers() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(
                get("/users")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", greaterThan(0)));
    }

    @Test
    void shouldReturn403WhenNoToken() throws Exception {
        mockMvc.perform(
                get("/users")
        )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(
                get("/users")
                        .header("Authorization", "Bearer token-invalido")
        )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        RolModel rol = rolRepository.findByName(RolName.TECNICO).get();
        String token = getAdminToken();
        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                      "name": "Diomedes",
                                      "lastName": "Díaz maestre",
                                      "email": "ElCacique2605@example.com",
                                      "password": "123456789",
                                      "documento": "1234567879",
                                      "phoneNumber": "12345633",
                                      "cargo": "empleado",
                                      "idRol": %d
                                }
                                """.formatted(rol.getIdRol())

                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ElCacique2605@example.com"))
                .andExpect(jsonPath("$.name").value("Diomedes"));

    }


    @Test
    void shouldThrowExceptionWhenUserWithEmailExists() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.TECNICO).get();
        UserModel existingUser = TestDataFactory.userRequest(rol);

        userRepository.save(existingUser);

        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "name": "Diomedes",
                                            "lastName": "Díaz maestre",
                                            "email": "ElCacique2605@example.com",
                                            "password": "123456789",
                                            "documento": "1234567879",
                                            "phoneNumber": "12345633",
                                            "cargo": "empleado",
                                            "idRol": %d
                                        }
                                        
                                """.formatted(rol.getIdRol())
                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with this email already exists"));

    }

    @Test
    void shouldThrowExceptiionWhenUserWithDocumentExists() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.TECNICO).get();
        UserModel user = TestDataFactory.userRequest(rol);
        userRepository.save(user);

        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "name": "Diomedes",
                                        "lastName": "Díaz maestre",
                                        "email": "ElCacique2605@example.com",
                                        "password": "123456789",
                                        "documento": "2222",
                                        "phoneNumber": "12345633",
                                        "cargo": "empleado",
                                        "idRol": %d
                                    }
                                
                                """.formatted(rol.getIdRol())
                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with this document already exists"));
    }



    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        RolModel rol= rolRepository.findByName(RolName.TECNICO).get();
        UserModel user = TestDataFactory.userRequest(rol);
        user = userRepository.save(user);
        RolModel newRol = rolRepository.findByName(RolName.ADMIN).get();


        String token = getAdminToken();

        mockMvc.perform(
                put("/users/" + user.getIdUsuario())
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "juanperez02@example.com",
                                      "name": "juan rodolfo",
                                      "active": true,
                                      "phoneNumber": "12345612",
                                      "cargo": "jefe",
                                      "idRol": %d
                                    }
                                
                                """.formatted(newRol.getIdRol())
                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juanperez02@example.com"))
                .andExpect(jsonPath("$.name").value("juan rodolfo"));

        UserModel updatedUser = userRepository.findById(user.getIdUsuario()).get();
        assertEquals("juanperez02@example.com", updatedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailExistsInUpdate() throws Exception {
        String token = getAdminToken();
        RolModel rol1 = rolRepository.findByName(RolName.ADMIN).get();
        RolModel rol2 = rolRepository.findByName(RolName.TECNICO).get();

        UserModel user1 = TestDataFactory.userRequest(rol1);
        user1 = userRepository.save(user1);

        UserModel user2 = TestDataFactory.userRequest(rol2);
        user2.setName("John");
        user2.setLastName("Doe");
        user2.setEmail("JohnDoe@example.com");
        user2.setDocumento("12345");
        user2.setCargo("empleado");
        user2.setPassword("123456");
        user2.setPhoneNumber("12253");
        user2.setActive(true);
        user2.setRol(rol2);

        user2 = userRepository.save(user2);


        mockMvc.perform(
                put("/users/" + user2.getIdUsuario())
                        .header("Authorization","Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                    
                                        "email": "ElCacique2605@example.com",
                                        "name": "juan rodolfo",
                                        "active": true,
                                        "phoneNumber": "12345612",
                                        "cargo": "empleado",
                                        "idRol": %d
                                    
                                    
                                    }        
                                """.formatted(rol2.getIdRol())

                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with this email already exist"));

    }


    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.TECNICO).get();
        UserModel user = TestDataFactory.userRequest(rol);
        user = userRepository.save(user);


        mockMvc.perform(
                delete("/users/" + user.getIdUsuario())
                        .header("Authorization", "Bearer " + token )

        ).andExpect(status().isOk());

        Optional<UserModel> deletedUser = userRepository.findById(user.getIdUsuario());
        assertTrue(deletedUser.isEmpty());

    }


    @Test
    void shouldThrowExceptionWhenUserNotFoundDelete() throws Exception {
        RolModel rol = rolRepository.findByName(RolName.TECNICO).get();
        String token = getAdminToken();
        UserModel user =TestDataFactory.userRequest(rol);
        user = userRepository.save(user);
        Long idInexistente = user.getIdUsuario() + 1000;

        mockMvc.perform(
                delete("/users/"+ idInexistente)
                        .header("Authorization", "Bearer "+ token)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró el user con el id: " + idInexistente));
    }

}
