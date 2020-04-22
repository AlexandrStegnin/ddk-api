package com.ddkolesnik.ddkapi;

import com.ddkolesnik.ddkapi.service.bitrix.BitrixContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Alexandr Stegnin
 */

@SpringBootTest
public class MergeContactsTest {

    @Autowired
    private BitrixContactService bitrixContactService;

    @Test
    public void merge() {
        bitrixContactService.updateContacts();
    }

}
