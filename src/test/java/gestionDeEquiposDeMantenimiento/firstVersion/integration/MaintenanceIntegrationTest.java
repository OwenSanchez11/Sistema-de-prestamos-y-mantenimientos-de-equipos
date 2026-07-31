package gestionDeEquiposDeMantenimiento.firstVersion.integration;


import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentStatus;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.MaintenanceModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.MaintenanceRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.MaintenanceStatus;
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

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MaintenanceIntegrationTest extends IntegrationTestBase{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    MaintenanceRepository maintenanceRepository;

    @Test
    void shouldGetMaintenanceSucessfully() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user = TestDataFactory.userRequest(rol);
        user = userRepository.save(user);
        MaintenanceModel maintenance = TestDataFactory.maintenanceExisting();
        maintenance.setEquipment(equipment);
        maintenance.setUserRegister(user);

        maintenance = maintenanceRepository.save(maintenance);

        mockMvc.perform(
                get("/maintenance")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", greaterThan(0)));

    }

    @Test
    void shouldCreateMaintenanceSuccessfully() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user = TestDataFactory.userRequest(rol);
        user = userRepository.save(user);

        mockMvc.perform(
                post("/maintenance")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "idEquipment": %d,
                                             "idUser": %d,
                                             "description": "Mantenimiento",
                                             "priceMaintenance": "1111"
                                        
                                        }
                                        
                                        
                                        """.formatted(equipment.getIdEquipment()
                                        ,user.getIdUsuario())
                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Mantenimiento"))
                .andExpect(jsonPath("$.maintenanceStatus").value("IN_PROGRESS"));



    }

    @Test
    void shouldThrowExceptionMaintenanceWhenEquipmentIsNotAvailable() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        equipment = equipmentRepository.save(equipment);
        UserModel user = TestDataFactory.userRequest(rol);
        user = userRepository.save(user);

        mockMvc.perform(
                post("/maintenance")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "idEquipment": %d,
                                             "idUser": %d,
                                             "description": "Mantenimiento",
                                             "priceMaintenance": "1111"
                                        
                                        }
                                        
                                        
                                        """.formatted(equipment.getIdEquipment()
                                        ,user.getIdUsuario())
                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The equipment is not available"));

    }

    @Test
    void shouldThrowExceptionMaintenanceWhenUserIsInactive() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user = TestDataFactory.userRequest(rol);
        user.setActive(false);
        user = userRepository.save(user);

        mockMvc.perform(
                post("/maintenance")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "idEquipment": %d,
                                             "idUser": %d,
                                             "description": "Mantenimiento",
                                             "priceMaintenance": "1111"
                                        
                                        }
                                        
                                        
                                        """.formatted(equipment.getIdEquipment()
                                        ,user.getIdUsuario())
                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User is disconnect"));


    }

    @Test
    void shouldUpdateMaintenanceSuccessfully() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user = TestDataFactory.userRequest(rol);
        user = userRepository.save(user);
        MaintenanceModel maintenance = TestDataFactory.maintenanceExisting();
        maintenance.setEquipment(equipment);
        maintenance.setUserRegister(user);

        maintenance = maintenanceRepository.save(maintenance);

        mockMvc.perform(
                put("/maintenance/" + maintenance.getIdMaintenance())
                        .header("Authorization", "Bearer " + token)

        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMaintenance").value(maintenance.getIdMaintenance()))
                .andExpect(jsonPath("$.maintenanceStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.endDate").isNotEmpty());
    }


    @Test
    void shouldThrowExceptionInUpdateWhenMaintenanceIsCompleted() throws Exception {
        String token = getAdminToken();
        RolModel rol = rolRepository.findByName(RolName.ADMIN).get();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment = equipmentRepository.save(equipment);
        UserModel user = TestDataFactory.userRequest(rol);
        user = userRepository.save(user);
        MaintenanceModel maintenance = TestDataFactory.maintenanceExisting();
        maintenance.setEquipment(equipment);
        maintenance.setUserRegister(user);
        maintenance.setMaintenanceStatus(MaintenanceStatus.COMPLETED);

        maintenance = maintenanceRepository.save(maintenance);

        mockMvc.perform(
                put("/maintenance/" + maintenance.getIdMaintenance())
                        .header("Authorization", "Bearer " + token)

        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Maintenance has already been completed."));


    }




}
