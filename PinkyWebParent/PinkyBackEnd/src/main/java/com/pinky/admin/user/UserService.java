package com.pinky.admin.user;

import com.pinky.common.entity.Role;
import com.pinky.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 4;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll(){
        return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
    }

    public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, USERS_PER_PAGE, sort);

        if(keyword != null){
            return userRepository.findAll(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }
    public List<Role> roleList(){
        return (List<Role>) roleRepository.findAll();
    }

    public User saveUser(User user){
        boolean isUpdating = (user.getId() != null);

        if(isUpdating){
            User existingUser = userRepository.findById(user.getId()).get();
            if(user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            } else {
                endcodePassword(user);
            }
        } else {
            endcodePassword(user);
        }

        return userRepository.save(user);

    }

    private void endcodePassword(User user){
        String endcodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(endcodePassword);
    }

    public boolean isEmailUnique(Integer id, String email){
        User userByEmail = userRepository.getUserByEmail(email);
        if(userByEmail == null) return true;
        boolean isCreatingNew = (id == null);
        if(isCreatingNew){
            if(userByEmail != null) return false;
        } else {
            if(userByEmail.getId() != id){
                return false;
            }
        }
        return true;
    }

    public User getUser(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException ex){
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
    }

    public void delete(Integer id) throws UserNotFoundException{
        Long countById = userRepository.countById(id);
        if(countById == null || countById == 0){
            throw new UserNotFoundException("Could not find any user with ID "+ id);
        }
        userRepository.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled){
        userRepository.updateEnabledStatus(id, enabled);

    }
}
