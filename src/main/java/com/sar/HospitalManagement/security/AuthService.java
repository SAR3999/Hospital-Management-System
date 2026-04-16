package com.sar.HospitalManagement.security;

import com.sar.HospitalManagement.dto.LoginRequestDto;
import com.sar.HospitalManagement.dto.LoginResponseDto;
import com.sar.HospitalManagement.dto.SignupRequestDto;
import com.sar.HospitalManagement.dto.SignupResponseDto;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.AuthProviderType;
import com.sar.HospitalManagement.entity.type.RoleType;
import com.sar.HospitalManagement.repository.PatientRepository;
import com.sar.HospitalManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        /*
        * It calls an AuthenticationProvider Usually → DaoAuthenticationProvider
          That calls your: UserDetailsService.loadUserByUsername(username) */
        Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
                /* It takes principle and credential to check that particular user is present in our database */
        );
        /*
        * After success:
        * This object now contains:
        * ✅ principal (user)
        * ✅ authorities (roles)
        * ✅ authenticated = true
        * */

        User user = (User) authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);

        return LoginResponseDto.builder()
                .jwt(token)
                .userId(user.getId())
                .build();
    }

    public User internalSignup(SignupRequestDto signupRequestDto, AuthProviderType authProviderType, String providerId) {
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if(user != null) throw new IllegalArgumentException("User already exists");

        user = User.builder()
                .username(signupRequestDto.getUsername())
                .providerId(providerId)
                .providerType(authProviderType)
                .roles(signupRequestDto.getRoles()) // Role.PATIENT
                .build();

        if(authProviderType == AuthProviderType.EMAIL) {
            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        }

        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getUsername())
                .user(user)
                .build();
        patientRepository.save(patient);

        return user;
    }

    // login Controller
    public SignupResponseDto signup(SignupRequestDto signupRequestDto){
        User user = internalSignup(signupRequestDto, AuthProviderType.EMAIL,null);
        //return modelMapper.map(user, SignupResponseDto.class);

        // for mapping with our control over which field to be match and in the case of different fields name on both classes.
        modelMapper.typeMap(User.class, SignupResponseDto.class)
                .addMapping(User::getId,SignupResponseDto::setId)       // if user has userId as field and dto class has id field. So, we can addMapping(User::getUserId, SignupResponseDto::setId);
                .addMapping(User::getUsername, SignupResponseDto::setUsername);

        return modelMapper.map(user, SignupResponseDto.class);
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistration(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        User user = userRepository.findByProviderIdAndProviderType(providerId,providerType).orElse(null);
        User emailUser = (email != null) ? userRepository.findByUsername(email).orElse(null) : null;

        if(user == null && emailUser == null){
            //SignUp
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User,registrationId,providerId);
            user = internalSignup(new SignupRequestDto(username, null,name, Set.of(RoleType.PATIENT)), providerType, providerId);
        } else if(user != null){
            if(email != null && !email.isBlank() && !email.equals(user.getUsername())){
                user.setUsername(email);
                userRepository.save(user);
            }
        } else{
            throw new BadCredentialsException("This email is already registered with provider "+email);
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto(authUtil.generateAccessToken(user),user.getId());
        return ResponseEntity.ok(loginResponseDto);
    }
}
