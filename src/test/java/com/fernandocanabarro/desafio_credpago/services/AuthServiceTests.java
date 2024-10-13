package com.fernandocanabarro.desafio_credpago.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.fernandocanabarro.desafio_credpago.dtos.AccountActivationResponse;
import com.fernandocanabarro.desafio_credpago.dtos.LoginRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.LoginResponseDTO;
import com.fernandocanabarro.desafio_credpago.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.UserDTO;
import com.fernandocanabarro.desafio_credpago.entities.ActivationCode;
import com.fernandocanabarro.desafio_credpago.entities.Cart;
import com.fernandocanabarro.desafio_credpago.entities.Role;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.factories.CartFactory;
import com.fernandocanabarro.desafio_credpago.factories.RoleFactory;
import com.fernandocanabarro.desafio_credpago.factories.UserFactory;
import com.fernandocanabarro.desafio_credpago.repositories.ActivationCodeRepository;
import com.fernandocanabarro.desafio_credpago.repositories.CartRepository;
import com.fernandocanabarro.desafio_credpago.repositories.RoleRepository;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ExpiredActivationCodeException;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ForbiddenException;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.desafio_credpago.utils.CustomUserUtils;

import java.time.LocalDateTime;
import java.time.Instant;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ActivationCodeRepository activationCodeRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserUtils customUserUtils;

    private RegistrationRequestDTO registrationRequestDTO;
    private User user;
    private Cart cart;
    private Role role;
    private String code;
    private ActivationCode activationCode;
    private LoginRequestDTO loginRequestDTO;
    private Authentication authentication;
    private Jwt jwt;

    @BeforeEach
    public void setup() throws Exception{
        registrationRequestDTO = new RegistrationRequestDTO("name", "email@gmail.com", "password");
        user = UserFactory.getUser();
        cart = CartFactory.getCart();
        role = RoleFactory.getUserRole();
        code = "12345";
        activationCode = new ActivationCode(1L, code, user, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30L), 
            false, null);
        loginRequestDTO = new LoginRequestDTO("email@gmail.com", "password");
        authentication = new UsernamePasswordAuthenticationToken("email@gmail.com", "password");
        jwt = Jwt.withTokenValue("token")
            .headers(headers -> {
                headers.put("alg","HS256");
                headers.put("typ", "JWT");
            })
            .issuer("login-auth-api")
            .subject(authentication.getName())
            .claim("username", authentication.getName())
            .claim("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(86400L))
            .build();
    }

    @Test
    public void registerShouldReturnUserDTO(){
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(roleRepository.findByAuthority(anyString())).thenReturn(role);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO response = authService.register(registrationRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("name");
        assertThat(response.getEmail()).isEqualTo("email@gmail.com");
        assertThat(response.getRoles().getFirst().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    public void activateAccountShouldReturnAccountActivationResponseWhenCodeIsValid(){
        when(activationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(activationCode));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(activationCodeRepository.save(any(ActivationCode.class))).thenReturn(activationCode);

        AccountActivationResponse response = authService.activateAccount(code);

        assertThat(response.getMessage()).isEqualTo("Conta ativada com sucesso");
    }

    @Test
    public void activateAccountShouldThrowResourceNotFoundExceptionWhenCodeDoesNotExist(){
        when(activationCodeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.activateAccount(code)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void activateAccountShouldThrowExpiredActivationCodeExceptionWhenCodeIsIsExpired(){
        activationCode.setExpiresAt(LocalDateTime.now().minusMinutes(1L));
        when(activationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(activationCode));
        
        assertThatThrownBy(() -> authService.activateAccount(code)).isInstanceOf(ExpiredActivationCodeException.class);
    }

    @Test
    public void loginShouldReturnLoginResponseDTOWhenUserIsActivated(){
        user.setActivated(true);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        LoginResponseDTO response = authService.login(loginRequestDTO);

        assertThat(response.getAccessToken()).isEqualTo(jwt.getTokenValue());
        assertThat(response.getExpiresIn()).isEqualTo(86400L);
    }

    @Test
    public void loginShouldThrowForbiddenExceptionWhenAccountIsNotActivated(){
        user.setActivated(false);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(loginRequestDTO)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void getConnectedUserShouldReturnUser(){
        String username = "email@gmail.com";
        when(customUserUtils.getLoggedUsername()).thenReturn(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        User response = authService.getConnectedUser();

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("name");
        assertThat(response.getEmail()).isEqualTo("email@gmail.com");
        assertThat(response.getPassword()).isEqualTo("password");
    }

    @Test
    public void getConnectedUserShouldThrowUsernameNotFoundExceptionWhenClassCastExceptionIsThrown(){
        when(customUserUtils.getLoggedUsername()).thenThrow(ClassCastException.class);

        assertThatThrownBy(() -> authService.getConnectedUser()).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    public void validateSelfOrAdminShouldThrowNoExceptionWhenUserIsAdmin(){
        user.addRole(RoleFactory.getAdminRole());
        long userId = user.getId();
        String username = user.getEmail();
        
        when(customUserUtils.getLoggedUsername()).thenReturn(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        assertThatCode(() -> authService.validateSelfOrAdmin(userId)).doesNotThrowAnyException();
    }

    @Test
    public void validateSelfOrAdminShouldThrowNoExceptionWhenUserIdIsEqualToConnectedUserId(){
        long userId = user.getId();
        String username = user.getEmail();
        
        when(customUserUtils.getLoggedUsername()).thenReturn(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        assertThatCode(() -> authService.validateSelfOrAdmin(userId)).doesNotThrowAnyException();
    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenUserIdIsNotEqualToConnectedUserId(){
        long userId = 999L;
        String username = user.getEmail();
        
        when(customUserUtils.getLoggedUsername()).thenReturn(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.validateSelfOrAdmin(userId)).isInstanceOf(ForbiddenException.class);
    }
}
