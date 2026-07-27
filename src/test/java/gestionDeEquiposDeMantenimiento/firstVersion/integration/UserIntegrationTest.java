package gestionDeEquiposDeMantenimiento.firstVersion.integration;


import gestionDeEquiposDeMantenimiento.firstVersion.Rol.RolRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

}
