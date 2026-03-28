package com.ecommerce.backend.security;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 *
 * <p>This service is used by the authentication manager to load user details
 * during login and by the JWT filter to validate tokens on each request.</p>
 *
 * <p>Since our {@link User} entity already implements {@link UserDetails},
 * we simply return it directly — no need for adapter classes.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address (used as the username in our system).
     *
     * <p>Called by:</p>
     * <ul>
     *   <li>AuthenticationManager during login (to compare credentials)</li>
     *   <li>JwtAuthenticationFilter on each request (to set the SecurityContext)</li>
     * </ul>
     *
     * @param email the user's email address
     * @return the User entity (which implements UserDetails)
     * @throws UsernameNotFoundException if no user with this email exists
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));
    }
}
