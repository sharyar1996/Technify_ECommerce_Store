package com.sharyar.Electrify.ElectronicsShop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

//Lombok will automatically make all getters/setters and constructors for us at runtime
//we just need to put below anotations:
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private String userId;

    @Column( nullable = false)
    private String userName;

    @Column(unique = true , length = 100 , nullable = false)
    private String email;

    @Column(length = 300 , nullable = false )
    private String password;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Cart myCart ;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private List<Order> orderHistory = new Stack<>();

    @OneToOne(cascade = CascadeType.REMOVE  , orphanRemoval = true, fetch = FetchType.EAGER)
    private  BillingAddress billingAddress;

    @ManyToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    //UserDetails Implementation variables
    private boolean isEnabled = true;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;

    public Set<Role> getRoles() {
        if(roles == null)
        {
            System.out.println("roles from getRoles in User is null");
        }
        return roles;
    }

    //METHODS FROM USER_DETAILS INTERFACE

    //IMPORTANT ! We must implement this method.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<SimpleGrantedAuthority> grantedAuthorities = roles.stream().map(role -> {
             return new SimpleGrantedAuthority(role.getRoleName());
        }).collect(Collectors.toSet());

        return grantedAuthorities;
    }

    public String getUserName() {
        return this.userName;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        isAccountNonExpired = true;
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        isAccountNonLocked = true;
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        isCredentialsNonExpired = true;
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        isEnabled = true;
        return isEnabled;
    }

}
