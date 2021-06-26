package com.pinky.admin.user;

import com.pinky.admin.FileUploadUtil;
import com.pinky.common.entity.Role;
import com.pinky.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/users")
    public String listUsers(Model model){
        List<User> userList = userService.listAll();
        model.addAttribute("listUsers", userList);

        return "users";
    }

    @RequestMapping("/users/new")
    public String createNewUser(Model model){
        User user = new User();
        user.setEnabled(true);
        List<Role> listRoles = userService.roleList();
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create new user");
        System.out.println(user);
        return "user_form";
    }

    @RequestMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser  = userService.saveUser(user);

            String uploadDir = "user-photo/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if(user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.saveUser(user);
        }

        redirectAttributes.addFlashAttribute("respond_message", "The user has been saved successfully");
        return "redirect:/users";
    }

    @RequestMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer userId, Model model, RedirectAttributes redirectAttributes){
        try {
            User user = userService.getUser(userId);
            List<Role> listRoles = userService.roleList();
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit user (ID: " + userId + ")");
            model.addAttribute("listRoles", listRoles);
            return "user_form";
        } catch (UserNotFoundException userNotFoundException){
            redirectAttributes.addFlashAttribute("respond_message", userNotFoundException.getMessage());
            return "redirect:/users";
        }

    }

    @RequestMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer userId,RedirectAttributes redirectAttributes){
        try {
            userService.delete(userId);
            redirectAttributes.addFlashAttribute("respond_message", "The user ID " + userId + " has been deleted successfully");
        } catch (UserNotFoundException userNotFoundException){
            redirectAttributes.addFlashAttribute("respond_message", userNotFoundException.getMessage());
        }
        return "redirect:/users";
    }

    @RequestMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes){
        userService.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user Id: " + id + " has been changed to " + status;
        redirectAttributes.addFlashAttribute("respond_message", message);
        return "redirect:/users";
    }
}
