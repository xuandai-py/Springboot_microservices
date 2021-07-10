package com.pinky.admin.user;

import com.pinky.admin.FileUploadUtil;
import com.pinky.common.entity.Role;
import com.pinky.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/users")
    public String listUsersPage(Model model){
        listUserPerPage(1, model, "firstName", "asc", null);
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

        return getRedirectURLToUser(user);
    }

    public String getRedirectURLToUser(User user){
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField =id&sortDir=asc&keyword=" + firstPartOfEmail;
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

    @RequestMapping("/users/page/{pageNum}")
    public String listUserPerPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                                  @Param("sortField") String sortField,
                                  @Param("sortDir") String sortDir,
                                  @Param("keyword") String keyword ){

        System.out.println("Sort field: " + sortField);
        System.out.println("Sort dir: " + sortDir);

        Page<User> usersPage = userService.listByPage(pageNum, sortField, sortDir, keyword);

        List<User> userListPerPage = usersPage.getContent();
        String reverseSortDir =  sortDir.equals("asc") ? "desc" : "asc";

        // start:(1,5,9,13...(x+=4, z+=1)) x=z+x-1
        // end : (4,8,12,16...)y
        long startCount = (pageNum - 1) *userService.USERS_PER_PAGE + 1 ;
        long endCount = startCount + userService.USERS_PER_PAGE -1;
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", usersPage.getTotalElements());
        model.addAttribute("listUsers", userListPerPage);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "users";
    }

    @RequestMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse httpServletResponse) throws IOException {
        List<User> users = userService.listAll();
        UserCSVExporter userCSVExporter = new UserCSVExporter();
        userCSVExporter.export(users, httpServletResponse);
    }

    @RequestMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse httpServletResponse) throws IOException {
        List<User> users = userService.listAll();
        UserExcelExporter userExcelExporter = new UserExcelExporter();
        userExcelExporter.export(users, httpServletResponse);
    }

    @RequestMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse httpServletResponse) throws IOException {
        List<User> users = userService.listAll();
        UserPDFExporter userPDFExporter = new UserPDFExporter();
        userPDFExporter.export(users, httpServletResponse);
    }

}













