
package gestionDeEquiposDeMantenimiento.firstVersion.Maintenance;

import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentStatus;
import gestionDeEquiposDeMantenimiento.firstVersion.Exceptions.BusinessRuleException;
import gestionDeEquiposDeMantenimiento.firstVersion.Exceptions.ResourceNotFoundException;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.DTO.MaintenanceCreateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.DTO.MaintenanceResponseDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    
    
    public Page<MaintenanceResponseDTO> obtenerMantenimientosPorPagina(int numPagina, int tamañoPagina, String sortBy, Sort.Direction direction, Long idEquipment) {
        Pageable pageable = PageRequest.of(numPagina, tamañoPagina, Sort.by(direction, sortBy));

        Specification<MaintenanceModel> spec = Specification.unrestricted();
        Page<MaintenanceModel> maintenance;
        spec = spec.and(MaintenanceSpecification.hasEquipmentId(idEquipment));

        maintenance = maintenanceRepository.findAll(spec, pageable);


        return maintenance.map(this::mapToResponse);
    }
    
    
    public MaintenanceResponseDTO getMaintenanceById(Long idMaintenance) {
        MaintenanceModel maintenance = maintenanceRepository.findById(idMaintenance).orElseThrow(() -> new ResourceNotFoundException("Maintennace not found"));
        
        return new MaintenanceResponseDTO(maintenance.getIdMaintenance(), 
                maintenance.getEquipment().getIdEquipment(), 
                maintenance.getUserRegister().getIdUsuario(), LocalDate.now(), 
                maintenance.getEndDate(),
                maintenance.getDescription(),
                maintenance.getPriceMaintenance(), maintenance.getMaintenanceStatus());
        
    }
    
    
    @Transactional
    public MaintenanceResponseDTO saveMaintenance(MaintenanceCreateDTO request) {
        MaintenanceModel maintenance = new MaintenanceModel();
        EquipmentModel equipment = equipmentRepository.findById(request.getIdEquipment()).orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
        UserModel user = userRepository.findById(request.getIdUser()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        

        if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
            throw new BusinessRuleException("The equipment is not available");
        }
        
        if (!user.getActive()) {
            throw  new BusinessRuleException("User is disconnect");
        }
       

        maintenance.setEquipment(equipment);
        maintenance.setUserRegister(user);
        maintenance.setDescription(request.getDescription());
        maintenance.setStartDate(LocalDate.now());
        maintenance.setMaintenanceStatus(MaintenanceStatus.IN_PROGRESS);
        maintenance.setPriceMaintenance(request.getPriceMaintenance());

        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        equipmentRepository.save(equipment);
        
        MaintenanceModel maintenanceSaved = maintenanceRepository.save(maintenance);
        
        return mapToResponse(maintenanceSaved);
        
        
    }
    
    private MaintenanceResponseDTO mapToResponse(MaintenanceModel maintenance) {
        return new MaintenanceResponseDTO(
                maintenance.getIdMaintenance(),
                maintenance.getEquipment().getIdEquipment(),
                maintenance.getUserRegister().getIdUsuario(),
                maintenance.getStartDate(),
                maintenance.getEndDate(),
                maintenance.getDescription(),
                maintenance.getPriceMaintenance(),
                maintenance.getMaintenanceStatus()
        );
}
    @Transactional
    public MaintenanceResponseDTO updateMaintenance(Long idMaintenance) {
        MaintenanceModel maintenance = maintenanceRepository.findById(idMaintenance).orElseThrow(() -> new ResourceNotFoundException("Maintenance Not Found"));

        if (maintenance.getMaintenanceStatus() == MaintenanceStatus.COMPLETED) {
            throw new BusinessRuleException("Maintenance has already been completed.");
        }

        EquipmentModel equipment = maintenance.getEquipment();
        maintenance.setMaintenanceStatus(MaintenanceStatus.COMPLETED);
        maintenance.setEndDate(LocalDate.now());
        
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(equipment);

        MaintenanceModel maintenanceSaved = maintenanceRepository.save(maintenance);
        
        return mapToResponse(maintenanceSaved);
    }
    
    
    
    

    
}
