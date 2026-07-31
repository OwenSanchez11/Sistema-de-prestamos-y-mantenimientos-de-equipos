package gestionDeEquiposDeMantenimiento.firstVersion.util;

import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.DTO.EquipmentCreateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.DTO.EquipmentUpdateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentStatus;
import gestionDeEquiposDeMantenimiento.firstVersion.Loan.LoanModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Loan.LoanStatus;
import gestionDeEquiposDeMantenimiento.firstVersion.LoanDTO.LoanCreateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.LoanDTO.LoanUpdateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.DTO.MaintenanceCreateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.MaintenanceModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Maintenance.MaintenanceStatus;
import gestionDeEquiposDeMantenimiento.firstVersion.Rol.RolModel;
import gestionDeEquiposDeMantenimiento.firstVersion.User.DTO.UserCreateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.User.DTO.UserUpdateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;

public class TestDataFactory {

    public static EquipmentModel availableEquipment() {
        EquipmentModel equipment = new EquipmentModel();
        equipment.setIdEquipment(1L);
        equipment.setActive(true);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        return equipment;
    }

    public static EquipmentModel maintenanceEquipment() {
        EquipmentModel equipment = availableEquipment();
        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        return equipment;
    }

    public static EquipmentModel loanedEquipment() {
        EquipmentModel equipment = availableEquipment();
        equipment.setStatus(EquipmentStatus.LOANED);
        return equipment;
    }

    public static UserModel activeUser(Long id) {
        UserModel user = new UserModel();
        user.setIdUsuario(id);
        user.setActive(true);
        user.setPhoneNumber("1111");
        return user;
    }

    public static UserModel InactiveUser(Long id) {
        UserModel user = activeUser(id);
        user.setActive(false);
        return user;
    }

    public static LoanCreateDTO validLoanRequest() {
        LoanCreateDTO dto = new LoanCreateDTO();
        dto.setIdEquipment(1L);
        dto.setIdUserReceiver(2L);
        dto.setIdUserDeliverer(3L);
        dto.setObservationsOut("equipo en buen estado");
        return dto;
    }

    public static MaintenanceCreateDTO validMaintenanceRquest() {
        MaintenanceCreateDTO maintenance = new MaintenanceCreateDTO();
        maintenance.setIdEquipment(1L);
        maintenance.setIdUser(2L);
        maintenance.setDescription("equipo en mantenimiento");
        maintenance.setPriceMaintenance("500");
        return maintenance;
    }

    public static MaintenanceModel mainteanance() {
        MaintenanceModel maint = new MaintenanceModel();
        maint.setIdMaintenance(3L);
        maint.setEquipment(maintenanceEquipment());
        maint.setUserRegister(activeUser(2L));
        maint.setMaintenanceStatus(MaintenanceStatus.IN_PROGRESS);
        return maint;
    }

    public static LoanModel loanRequestForUpdate() {
        LoanModel loan = new LoanModel();
        loan.setIdLoan(3L);
        loan.setEquipment(loanedEquipment());
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setUserDeliverer(activeUser(2L));
        loan.setUserReceiver(activeUser(3L));
        return loan;
    }

    public static LoanUpdateDTO validLoanUpdateRequest() {
        LoanUpdateDTO request = new LoanUpdateDTO();
        request.setObservationsReturn("Equipo devuelto en buen estado");
        return request;
    }

    public static EquipmentCreateDTO validEquipmentRequest() {
        EquipmentCreateDTO equipment = new EquipmentCreateDTO();
        equipment.setName("Taldro");
        equipment.setModel("1111");
        equipment.setBrand("222");
        equipment.setLocation("b site");
        equipment.setDescription("taladro de 5 niveles");
        equipment.setCodeInventory(1111);
        equipment.setSeriesNum(123456);

        return equipment;
    }

    public static EquipmentUpdateDTO validUpdateEquipmentRequest() {
        EquipmentUpdateDTO equipment = new EquipmentUpdateDTO();
        equipment.setActive(false);
        return equipment;
    }

    public static UserCreateDTO validUserRequest(Long idRol) {
        UserCreateDTO request = new UserCreateDTO();
        request.setName("user1");
        request.setLastName("userApellido");
        request.setEmail("user1@user.com");
        request.setDocumento("1111");
        request.setPassword("123456");
        request.setDocumento("2222");
        request.setPhoneNumber("3333");
        request.setIdRol(idRol);
        request.setCargo("Jefe");

        return request;
    }

    public static UserUpdateDTO validUserUpdateRequest() {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setName("User2");
        user.setPhoneNumber("5555");
        user.setEmail("user2@user2.com");
        user.setIdRol(2L);
        user.setCargo("empleado");
        user.setActive(true);

        return user;
    }

    public static UserModel userRequest(RolModel rol) {
        UserModel user = new UserModel();
        user.setName("Alberto");
        user.setLastName("sobrnino");
        user.setEmail("ElCacique2605@example.com");
        user.setDocumento("2222");
        user.setCargo("empleado");
        user.setPassword("123456789");
        user.setPhoneNumber("1111");
        user.setRol(rol);
        user.setActive(true);

        return user;
    }

    public static EquipmentModel equipmentExisting()  {
        EquipmentModel equipment = new EquipmentModel();
        equipment.setName("Taladro");
        equipment.setDescription("taladro 2 piezas");
        equipment.setBrand("123456");
        equipment.setActive(true);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setModel("taladro cat");
        equipment.setCodeInventory(1111);
        equipment.setSeriesNum(2222);

        return equipment;
    }

    public static LoanModel loanExisting() {
        LoanModel loan = new LoanModel();
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setObservationsOut("se encuentra en perfecto estado el equipo");
        return loan;
    }

    public static MaintenanceModel maintenanceExisting() {
        MaintenanceModel maintenance = new MaintenanceModel();
        maintenance.setMaintenanceStatus(MaintenanceStatus.IN_PROGRESS);
        maintenance.setDescription("Mantenimiento");
        maintenance.setPriceMaintenance("1111");

        return maintenance;
    }

}
