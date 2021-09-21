package com.ddkolesnik.ddkapi.model.security;

import com.ddkolesnik.ddkapi.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

import static com.ddkolesnik.ddkapi.util.Constant.ROLE_PREFIX;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "app_role", schema = "investments", catalog = "investments")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
public class Role extends AbstractEntity implements GrantedAuthority {

    @Id
    private Long id;

    @Column(name = "name")
    private String title;

    @Override
    public String getAuthority() {
        return title.startsWith(ROLE_PREFIX) ? title : ROLE_PREFIX + title;
    }

    @PrePersist
    public void setRole() {
        if (!title.trim().toUpperCase().startsWith(ROLE_PREFIX)) title = ROLE_PREFIX + title.trim().toUpperCase();
        else title = title.trim().toUpperCase();
    }

    public Role(GrantedAuthority authority) {
        this.title = authority.getAuthority();
    }
}
