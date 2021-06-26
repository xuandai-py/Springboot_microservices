package com.pinky.admin.user;

import com.pinky.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository repository;

    @Test
    public void testCreateRole(){
        Role roleAdmin = new Role("Admin", "manage everything");
        Role saveRole = repository.save(roleAdmin);

        assertThat(saveRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSecondRole(){
        Role roleSalesperson = new Role("Salesperson", "manage product price - customers - shipping");
        Role roleEditor = new Role("Editor", "manage categories, brands, products articles and menus");
        Role roleShipper = new Role("Shipper", "view products, view orders, update order status");
        Role roleAssistant = new Role("Assistant", "manage Q&A review");

        repository.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));


    }
}
