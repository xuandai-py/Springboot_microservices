package com.pinky.admin.user;

import com.pinky.common.entity.Role;
import com.pinky.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void testAddUser(){
        Role roleADmin = testEntityManager.find(Role.class, 3);
        User user = new User("aaa@gmail.com", "0000", "dai", "xuan");
        user.addRole(roleADmin);

        User newUser = repository.save(user);

    }

    @Test
    public void testAddUserWithMoreRoles(){
        Role roleEditor = testEntityManager.find(Role.class, 5);
        Role roleAssistant = testEntityManager.find(Role.class, 7);
        User user = new User("Dai@gmail.com", "0000", "xd", "dx");

        user.addRole(roleEditor);
        user.addRole(roleAssistant);

        User newUser = repository.save(user);
        assertThat(newUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers(){
        Iterable<User> ltUsers = repository.findAll();
        ltUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById(){
        User findUser = repository.findById(1).get();
        System.out.println(findUser);
        assertThat(findUser).isNotNull();
    }

    @Test
    public void testUpdateUserDetails(){
        User user = repository.findById(1).get();
        user.setEnabled(true);
        user.setFirstName("ksksksksksk");

        repository.save(user);
    }

    @Test
    public void testEmail(){
        String email = "aaa@gmail.com";
        User user = repository.getUserByEmail(email);

        assertThat(user).isNotNull();
    }
}
