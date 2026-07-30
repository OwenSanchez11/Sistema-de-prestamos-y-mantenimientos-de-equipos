package gestionDeEquiposDeMantenimiento.firstVersion.Service;

import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Equipment.EquipmentStatus;
import gestionDeEquiposDeMantenimiento.firstVersion.Exceptions.BusinessRuleException;
import gestionDeEquiposDeMantenimiento.firstVersion.Exceptions.ResourceNotFoundException;
import gestionDeEquiposDeMantenimiento.firstVersion.Rol.RolModel;
import gestionDeEquiposDeMantenimiento.firstVersion.Rol.RolRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.User.DTO.UserCreateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.User.DTO.UserResponseDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.User.DTO.UserUpdateDTO;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserModel;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserRepository;
import gestionDeEquiposDeMantenimiento.firstVersion.User.UserService;
import gestionDeEquiposDeMantenimiento.firstVersion.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;



    @Test
    void shouldSaveUserSuccesfully() {

        UserCreateDTO request = TestDataFactory.validUserRequest(1L);
        RolModel rol = new RolModel();
        rol.setIdRol(1L);

        when(rolRepository.findById(request.getIdRol()))
                .thenReturn(Optional.of(rol));

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("PasswordCodificada");

        when(userRepository.save(any(UserModel.class)))
                .thenAnswer(invocation -> {
                    UserModel user = invocation.getArgument(0);
                    user.setActive(true);
                    return user;
                });

        UserResponseDTO response = userService.saveUser(request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertTrue(response.getActive());

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(UserModel.class));

    }

    @Test
    void shouldThrowExceptionWhenUserExistsByDocument() {
        UserCreateDTO request = TestDataFactory.validUserRequest(1L);
        RolModel rol = new RolModel();
        rol.setIdRol(1L);

        when(rolRepository.findById(request.getIdRol()))
                .thenReturn(Optional.of(rol));


        when(userRepository.existsByDocumento(request.getDocumento())).
                thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.saveUser(request));
        assertEquals("A user with this document already exists", exception.getMessage());

        verify(userRepository, never()).save(any(UserModel.class));

    }

    @Test
    void shouldThrowExceptionWhenUserExistsbyEmail() {
        UserCreateDTO request = TestDataFactory.validUserRequest(1L);
        RolModel rol = new RolModel();
        rol.setIdRol(1L);

        when(rolRepository.findById(request.getIdRol()))
                .thenReturn(Optional.of(rol));


        when(userRepository.existsByEmail(request.getEmail())).
                thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.saveUser(request));
        assertEquals("A user with this email already exists", exception.getMessage());

        verify(userRepository, never()).save(any(UserModel.class));

    }


    @Test
    void shouldThrowExceptionWhenRolIsNotFound() {
        UserCreateDTO request = TestDataFactory.validUserRequest(1L);

        when(rolRepository.findById(request.getIdRol()))
                .thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.saveUser(request));
        assertEquals("Rol no encontrado", exception.getMessage());

        verify(userRepository, never()).save(any(UserModel.class));

    }



    @Test
    void shouldCompleteUpdateUserSuccesfully() {
        UserUpdateDTO request = TestDataFactory.validUserUpdateRequest();
        UserModel user = TestDataFactory.activeUser(1L);
        RolModel rol = new RolModel();

        when(userRepository.findById(user.getIdUsuario()))
                .thenReturn(Optional.of(user));

        when(rolRepository.findById(request.getIdRol()))
                .thenReturn(Optional.of(rol));

        when(userRepository.save(any(UserModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)
                );

        UserModel response = userService.updateUser(request, user.getIdUsuario());

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertTrue(response.getActive());
        assertEquals(request.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(request.getCargo(), response.getCargo());

        verify(userRepository).save(any(UserModel.class));
        verify(rolRepository).findById(request.getIdRol());
        verify(passwordEncoder, never()).encode(anyString());

    }





    @Test
    void shouldThrowExceptionWhenEmailExistInUserUpdate() {
        UserUpdateDTO request = TestDataFactory.validUserUpdateRequest();
        UserModel user = TestDataFactory.activeUser(1L);
        RolModel rol = new RolModel();

        when(userRepository.findById(user.getIdUsuario()))
                .thenReturn(Optional.of(user));

        when(rolRepository.findById(request.getIdRol()))
                .thenReturn(Optional.of(rol));

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.updateUser(request, user.getIdUsuario()));
        assertEquals("A user with this email already exist", exception.getMessage());

        verify(userRepository, never()).save(any(UserModel.class));

    }


}
