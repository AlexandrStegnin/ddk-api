package com.ddkolesnik.ddkapi.model.bitrix;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "bitrix_contact_email", schema = "bitrix_flows", catalog = "bitrix_flows")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = {"contact"})
public class BitrixEmail {

    @Id
    String id;

    @Column(name = "value_type")
    String type;

    @Column(name = "value")
    String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    BitrixContact contact;

}
