package com.gaurav.authorizationserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="ROLE")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name="ROLE_CODE")
    private String role_code;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_mtm_permission",
            joinColumns = {
                    @JoinColumn(name = "role_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "permission_id")
            }
    )
    private List<Permission> permissions;
}
