package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User entity — represents a registered user in the e-commerce platform.
 *
 * <p>Implements {@link UserDetails} so Spring Security can directly use this
 * entity for authentication and authorization without a separate adapter.</p>
 *
 * <h3>Relationships:</h3>
 * <ul>
 *   <li>One-to-One with {@link Cart} — each user has exactly one cart</li>
 *   <li>One-to-Many with {@link Order} — a user can place many orders</li>
 * </ul>
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User's first name — required */
    @Column(nullable = false, length = 50)
    private String firstName;

    /** User's last name — required */
    @Column(nullable = false, length = 50)
    private String lastName;

    /** Unique email address — used as the username for authentication */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** BCrypt-hashed password — never returned in API responses */
    @Column(nullable = false)
    private String password;

    /** Phone number for contact/shipping purposes */
    @Column(length = 20)
    private String phone;

    /** Default shipping address */
    @Column(length = 500)
    private String address;

    /**
     * User role for authorization. Stored as a plain string (e.g., "ROLE_USER", "ROLE_ADMIN").
     * Spring Security's {@code hasRole("USER")} automatically matches "ROLE_USER".
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "ROLE_USER";

    /** Timestamp when the user account was created — set automatically */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last profile update — set automatically */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ======================== RELATIONSHIPS ========================

    /**
     * User's shopping cart. Cascade ALL ensures the cart is created/deleted
     * with the user. orphanRemoval cleans up if cart is set to null.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    /**
     * All orders placed by this user. Ordered by creation date descending
     * in the repository layer.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    // ======================== USERDETAILS IMPLEMENTATION ========================

    /**
     * Returns the authorities (roles) granted to the user.
     * Spring Security uses this to enforce role-based access control.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    /** Email is used as the username for Spring Security authentication */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
