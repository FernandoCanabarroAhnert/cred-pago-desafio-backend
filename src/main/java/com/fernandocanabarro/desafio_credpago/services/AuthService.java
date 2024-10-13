package com.fernandocanabarro.desafio_credpago.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_credpago.dtos.AccountActivationResponse;
import com.fernandocanabarro.desafio_credpago.dtos.LoginRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.LoginResponseDTO;
import com.fernandocanabarro.desafio_credpago.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.UserDTO;
import com.fernandocanabarro.desafio_credpago.entities.ActivationCode;
import com.fernandocanabarro.desafio_credpago.entities.Cart;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.repositories.ActivationCodeRepository;
import com.fernandocanabarro.desafio_credpago.repositories.CartRepository;
import com.fernandocanabarro.desafio_credpago.repositories.RoleRepository;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ExpiredActivationCodeException;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ForbiddenException;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.desafio_credpago.utils.CustomUserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final CartRepository cartRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserUtils customUserUtils;

    @Transactional
    public UserDTO register(RegistrationRequestDTO dto){
        User user = new User();
        toEntity(user,dto);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProducts(new ArrayList<>(Arrays.asList()));
        cartRepository.save(cart);

        user = userRepository.save(user);
        sendConfirmationEmail(user);
        return new UserDTO(user);
    }

    private void toEntity(User user, RegistrationRequestDTO dto) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActivated(false);
        user.addRole(roleRepository.findByAuthority("ROLE_USER"));
        user.setCreditCards(new ArrayList<>(Arrays.asList()));
        user.setTransactions(new ArrayList<>(Arrays.asList()));
    }

    private void sendConfirmationEmail(User user) {
        String code = generateAndSaveActivationCode(user);
        emailService.sendEmail(user.getFullName(), user.getEmail(), code);
    }

    @Transactional
    private String generateAndSaveActivationCode(User user) {
        String code = generateCode();
        ActivationCode activationCode = new ActivationCode();
        activationCode.setCode(code);
        activationCode.setUser(user);
        activationCode.setCreatedAt(LocalDateTime.now());
        activationCode.setExpiresAt(LocalDateTime.now().plusMinutes(30L));
        activationCode.setValidated(false);
        activationCode.setValidatedAt(null);
        activationCodeRepository.save(activationCode);
        return code;
    }

    private String generateCode() {
        String characters = "0123456789";
        int size = characters.length();
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++){
            int randomIndex = secureRandom.nextInt(size);
            stringBuilder.append(characters.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    @Transactional
    public AccountActivationResponse activateAccount(String code){
        ActivationCode activationCode = activationCodeRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Código não encontrado"));
        User user = activationCode.getUser();
        if (!activationCode.isValid()) {
            sendConfirmationEmail(user);
            throw new ExpiredActivationCodeException(user.getEmail());
        }
        user.setActivated(true);
        activationCode.setValidatedAt(LocalDateTime.now());
        activationCode.setValidated(true);
        userRepository.save(user);
        activationCodeRepository.save(activationCode);
        return new AccountActivationResponse("Conta ativada com sucesso");
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto){
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(dto.getEmail(), dto.getPassword());
        Authentication response = authenticationManager.authenticate(authentication);
        User user = userRepository.findByEmail(dto.getEmail()).get();
        if (!user.getActivated()) {
            throw new ForbiddenException("Conta não foi ativada");
        }
        var claims = JwtClaimsSet.builder()
            .issuer("login-auth-api")
            .subject(response.getName())
            .claim("username", response.getName())
            .claim("authorities", response.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(86400L))
            .build();
        String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new LoginResponseDTO(jwt, 86400L);
    }

    public User getConnectedUser(){
        try{
            String username = customUserUtils.getLoggedUsername();
            return userRepository.findByEmail(username).get();
        }
        catch (Exception e){
            throw new UsernameNotFoundException("User not found");
        }
    }

    public void validateSelfOrAdmin(Long userId){
        User user = getConnectedUser();
        if (user.hasRole("ROLE_ADMIN")) {
            return;
        }
        if (!user.getId().equals(userId)) {
            throw new ForbiddenException("Somente o Proprietário do Item ou o Adminstrador pode executar esta ação");
        }
    }
}
