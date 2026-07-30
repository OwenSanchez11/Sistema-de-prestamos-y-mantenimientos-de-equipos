package gestionDeEquiposDeMantenimiento.firstVersion.integration;


import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentStatus;
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
public class EquipmentIntegrationTest extends IntegrationTestBase{

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EquipmentRepository equipmentRepository;


    @Test
    void shouldGetAllEquipments() throws Exception {
        String token = getAdminToken();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipmentRepository.save(equipment);

        equipmentRepository.save(equipment);

        mockMvc.perform(
                get("/equipments")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", greaterThan(0)));


    }

    @Test
    void shouldCreateEquipmentSuccessfully() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(
                post("/equipments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(

                                """
                                      {
                                        "name": "Impresora Láser",
                                        "codeInventory": 45012,
                                        "description": "Monocromática, conexión Wi-Fi, capacidad de 250 hojas.",
                                        "brand": "HP",
                                        "model": "LaserJet Pro M404dw",
                                        "seriesNum": 7891011,
                                        "location": "Laboratorio de Sistemas - Aula 302"
                                      }
                                """

                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Impresora Láser"));

    }


    @Test
    void shouldThrowExceptionWhenEquipmentAlreadyExistsByCodeInventory() throws Exception {
        String token = getAdminToken();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment.setCodeInventory(45012);
        equipmentRepository.save(equipment);

        mockMvc.perform(
                post("/equipments")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                     {
                                        "name": "Impresora Láser",
                                        "codeInventory": 45012,
                                        "description": "Monocromática, conexión Wi-Fi, capacidad de 250 hojas.",
                                        "brand": "HP",
                                        "model": "LaserJet Pro M404dw",
                                        "seriesNum": 7891011,
                                        "location": "Laboratorio de Sistemas - Aula 302"
                                      }   
                                          
                                        
                                """

                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A equipment with that code already Exists"));
    }



    @Test
    void shouldUpdateEquipmentSuccesfully() throws Exception {
        String token = getAdminToken();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipmentRepository.save(equipment);

        mockMvc.perform(
                put("/equipments/" + equipment.getIdEquipment())
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "active": false
                                     }
                                """
                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));;
    }


    @Test
    void shouldThrowExceptionWhenEquipmentIsMaintenance() throws Exception {
        String token = getAdminToken();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        equipmentRepository.save(equipment);

        mockMvc.perform(
                put("/equipments/" + equipment.getIdEquipment())
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "active": false
                                    }
                                """
                        )
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The equipment is being used"));

    }

    @Test
    void shouldThrowExceptionWhenEquipmentIsLoaned() throws Exception {
        String token = getAdminToken();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipment.setStatus(EquipmentStatus.LOANED);
        equipmentRepository.save(equipment);

        mockMvc.perform(
                        put("/equipments/" + equipment.getIdEquipment())
                                .header("Authorization", "Bearer "+ token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                            {
                                                "active": false
                                            }
                                        """
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The equipment is being used"));

    }


    @Test
    void shouldDeleteSuccessfully() throws Exception {
        String token = getAdminToken();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipmentRepository.save(equipment);

        mockMvc.perform(
                delete("/equipments/" + equipment.getIdEquipment())
                        .header("Authorization", "Bearer "+ token)
        )
                .andExpect(status().isOk());
        Optional<EquipmentModel> deletedEquipment = equipmentRepository.findById(equipment.getIdEquipment());
        assertTrue(deletedEquipment.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenEquipmentNotFoundToDelete() throws Exception {
        String token = getAdminToken();
        EquipmentModel equipment = TestDataFactory.equipmentExisting();
        equipmentRepository.save(equipment);
        Long idInexistente = equipment.getIdEquipment() + 1000;

        mockMvc.perform(
                delete("/equipments/" + idInexistente)
                        .header("Authorization", "Bearer "+ token)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró un equipo con el id: " + idInexistente));
    }

}
