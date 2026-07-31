package gestionDeEquiposDeMantenimiento.firstVersion.integration;

import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentStatus;
import gestionDeEquiposDeMantenimiento.firstVersion.Loan.LoanModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Loan.LoanRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.Loan.LoanStatus;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LoanIntegratonTest extends IntegrationTestBase{

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    LoanRepository loanRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolRepository rolRepository;

    @Test
    void shouldGetLoanSuccessfully() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user1 = TestDataFactory.userRequest(rol);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("ejemplo");
        user2 = userRepository.save(user2);
        LoanModel loan = TestDataFactory.loanExisting();
        loan.setEquipment(equipment);
        loan.setUserReceiver(user1);
        loan.setUserDeliverer(user2);

        loan = loanRepository.save(loan);

        mockMvc.perform(
                get("/loan")
                        .header("Authorization", "Bearer "+ token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", greaterThan(0)));




    }

    @Test
    void shouldCreateLoanSuccessfully() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user1 = TestDataFactory.userRequest(rol);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("ejemplo");
        user2 = userRepository.save(user2);

        mockMvc.perform(
                post("/loan")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                    
                                        "idEquipment": %d,
                                        "idUserReceiver": %d,
                                        "idUserDeliverer": %d,
                                        "observationsOut": "El equipo se entrega con estuche rígido y cargador original. Sin rayones visibles."
                                        
                                    }      
                                
                                """.formatted(equipment.getIdEquipment()
                                        , user1.getIdUsuario()
                                        , user2.getIdUsuario())

                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiverName").value("Alberto"))
                .andExpect(jsonPath("$.delivererName").value("ejemplo"))
                .andExpect(jsonPath("$.observationsOut").value("El equipo se entrega con estuche rígido y cargador original. Sin rayones visibles."));

    }

    @Test
    void shoudlThrowExceptionWhenEquipmentIsNotAvailable() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment.setStatus(EquipmentStatus.LOANED);
        equipment = equipmentRepository.save(equipment);
        UserModel user1 = TestDataFactory.userRequest(rol);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("ejemplo");
        user2 = userRepository.save(user2);

        mockMvc.perform(
                post("/loan")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(

                                """
                                        {
                                            "idEquipment": %d,
                                            "idUserReceiver": %d,
                                            "idUserDeliverer": %d,
                                            "observationsOut": "El equipo se entrega con estuche rígido y cargador original. Sin rayones visibles."
                                        }
                                """.formatted(equipment.getIdEquipment()
                                        , user1.getIdUsuario()
                                        , user2.getIdUsuario())

                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The equipment is not available"));


    }


    @Test
    void shouldThrowExceptionWhenUserReceiverIsInactive() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user1 = TestDataFactory.userRequest(rol);
        user1.setActive(false);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("ejemplo");
        user2 = userRepository.save(user2);

        mockMvc.perform(
                post("/loan")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(

                                """
                                        {
                                        
                                            "idEquipment": %d,
                                            "idUserReceiver": %d,
                                            "idUserDeliverer": %d,
                                            "observationsOut": "El equipo se entrega con estuche rígido y cargador original. Sin rayones visibles."
                                        
                                        
                                        }
                                        
                                        
                                        """.formatted(equipment.getIdEquipment()
                                        , user1.getIdUsuario()
                                        , user2.getIdUsuario())
                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User is inactive"));




    }


    @Test
    void shouldThrowExceptionWhenUserDelivererIsInactivve() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user1 = TestDataFactory.userRequest(rol);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setActive(false);
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("ejemplo");
        user2 = userRepository.save(user2);

        mockMvc.perform(
                post("/loan").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(

                                """
                                        {
                                        
                                            "idEquipment": %d,
                                            "idUserReceiver": %d,
                                            "idUserDeliverer": %d,
                                            "observationsOut": "El equipo se entrega con estuche rígido y cargador original. Sin rayones visibles."
                                        
                                        
                                        }
                                        
                                        
                                        """.formatted(equipment.getIdEquipment()
                                        , user1.getIdUsuario()
                                        , user2.getIdUsuario())
            )


        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User is inactive"));




    }


    @Test
    void shouldUpdateLoanSuccessfully() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user1 = TestDataFactory.userRequest(rol);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setActive(false);
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("Albert Camus");
        user2 = userRepository.save(user2);
        LoanModel loan = TestDataFactory.loanExisting();
        loan.setEquipment(equipment);
        loan.setUserReceiver(user1);
        loan.setUserDeliverer(user2);

        loan = loanRepository.save(loan);


        mockMvc.perform(
                put("/loan/" + loan.getIdLoan())
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(

                                """
                                        {
                                          "observationsReturn": "el equipo volvió en buen estado"
                                        }
                                        
                                        
                                        """
                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.observationsReturn").value("el equipo volvió en buen estado"))
                .andExpect(jsonPath("$.delivererName").value("Albert Camus"));



    }



    @Test
    void shouldThrowExceptionWhenLoanIsReturned() throws Exception {
        String token = getAdminToken();

        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();

        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);

        UserModel user1 = TestDataFactory.userRequest(rol);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setActive(false);
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("Albert Camus");
        user2 = userRepository.save(user2);

        LoanModel loan = TestDataFactory.loanExisting();
        loan.setEquipment(equipment);
        loan.setUserReceiver(user1);
        loan.setLoanStatus(LoanStatus.RETURNED);
        loan.setUserDeliverer(user2);
        loan = loanRepository.save(loan);


        mockMvc.perform(
                put("/loan/" + loan.getIdLoan())
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(

                                """
                                        {
                                          "observationsReturn": "el equipo volvió en buen estado"
                                        }
                                        
                                        
                                        """
                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Loan already returned"));





    }


    @Test
    void shouldDeleteLoanSuccessfully() throws Exception {
        String token = getAdminToken();

        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();

        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);

        UserModel user1 = TestDataFactory.userRequest(rol);
        UserModel user2 = TestDataFactory.userRequest(rol);
        user1 = userRepository.save(user1);
        user2.setDocumento("3333");
        user2.setActive(false);
        user2.setEmail("elejemplo@gmail.com");
        user2.setName("Albert Camus");
        user2 = userRepository.save(user2);

        LoanModel loan = TestDataFactory.loanExisting();
        loan.setEquipment(equipment);
        loan.setUserReceiver(user1);
        loan.setLoanStatus(LoanStatus.RETURNED);
        loan.setUserDeliverer(user2);
        loan = loanRepository.save(loan);

        mockMvc.perform(
                delete("/loan/" + loan.getIdLoan())
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk());
        Optional<LoanModel> deleteLoan = loanRepository.findById(loan.getIdLoan());
        assertTrue(deleteLoan.isEmpty());


    }



}
