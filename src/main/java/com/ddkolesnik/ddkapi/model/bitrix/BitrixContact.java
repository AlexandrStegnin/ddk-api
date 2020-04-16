package com.ddkolesnik.ddkapi.model.bitrix;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "bitrix_contact", schema = "bitrix_flows", catalog = "bitrix_flows")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BitrixContact {

    @Id
    String id;

    @Column(name = "contact_name")
    String name;

    @Column(name = "contact_second_name")
    String secondName;

    @Column(name = "contact_last_name")
    String lastName;

    @Column(name = "partner_code")
    String code;

    @OneToMany(mappedBy = "contact", fetch = FetchType.EAGER)
    Set<BitrixEmail> emails;
}
