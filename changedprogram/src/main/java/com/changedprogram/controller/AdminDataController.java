package com.changedprogram.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.component.EncryptionUtil;
import com.changedprogram.dto.ResultDTO;
import com.changedprogram.dto.UserrDTO;
import com.changedprogram.entity.Result;
import com.changedprogram.entity.User;
import com.changedprogram.repository.ResultRepository;
import com.changedprogram.repository.UserRepository;
import com.changedprogram.service.AdminDataService;


@Controller  // Marks this class as a Spring MVC controller
@RequestMapping("/admin/datas")  // Maps all requests in this controller to /admin/datas
public class AdminDataController {

    @Autowired  // Injects the AdminDataService bean
    private AdminDataService adminDataService;

    @Autowired  // Injects the UserRepository bean
    private UserRepository userRepository;

    @Autowired  // Injects the ResultRepository bean
    private ResultRepository resultRepository;

    @Autowired  // Injects the EncryptionUtil bean
    private EncryptionUtil encryptionUtil;

    @GetMapping("/data")  // Handles GET requests for /admin/datas/data
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public List<UserrDTO> getAllUsers() {
        return adminDataService.getAllUsers();  // Returns a list of all users
    }

    @GetMapping("/results/{userId}")  // Handles GET requests for /admin/datas/results/{userId}
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public List<ResultDTO> getResultsByUserId(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));  // Retrieves the user by ID or throws an exception if not found
        return user.getResults().stream()
                .map(result -> {
                    String decryptedSignature = "";
                    if (result.getSignature() != null) {
                        try {
                            decryptedSignature = encryptionUtil.decrypt(result.getSignature());  // Decrypts the signature
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new ResultDTO(
                            result.getId(),
                            result.getFilled(),
                            result.isNotificationSent(),
                            result.getValid(),
                            result.getPpt() != null ? result.getPpt().getFilename() : null,
                            decryptedSignature,
                            result.getPpt() != null && result.getPpt().getLanguage() != null ? result.getPpt().getLanguage().getName() : null,
                            result.getPpt() != null && result.getPpt().getType() != null ? result.getPpt().getType().getName() : null
                    );
                })
                .collect(Collectors.toList());  // Collects and returns a list of ResultDTOs
    }

    @GetMapping("/pptdetails/{pptId}")  // Handles GET requests for /admin/datas/pptdetails/{pptId}
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public ResultDTO getPptDetails(@PathVariable Long pptId) {
        Result result = resultRepository.findById(pptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found"));  // Retrieves the result by ID or throws an exception if not found
        String decryptedSignature = "";
        if (result.getSignature() != null) {
            try {
                decryptedSignature = encryptionUtil.decrypt(result.getSignature());  // Decrypts the signature
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ResultDTO(
                result.getId(),
                result.getFilled(),
                result.isNotificationSent(),
                result.getValid(),
                result.getPpt() != null ? result.getPpt().getFilename() : null,
                decryptedSignature,
                result.getPpt() != null && result.getPpt().getLanguage() != null ? result.getPpt().getLanguage().getName() : null,
                result.getPpt() != null && result.getPpt().getType() != null ? result.getPpt().getType().getName() : null
        );
    }

    @GetMapping  // Handles GET requests for /admin/datas
    public String getAdminDatasPage(Model model) {
        return "adminDatas";  // Returns the view name for the admin datas page
    }
    
    // Modify data
    @GetMapping("/modifydata")  // Handles GET requests for /admin/datas/modifydata
    public String showModifyDataForm(@RequestParam(required = false) Long userId, Model model) {
        model.addAttribute("users", adminDataService.getAllUsers());  // Adds all users to the model
        model.addAttribute("positions", adminDataService.getAllPositions());  // Adds all positions to the model
        model.addAttribute("companies", adminDataService.getAllCompanies());  // Adds all companies to the model
        model.addAttribute("receivers", adminDataService.getAllReceivers());  // Adds all receivers to the model

        if (model.containsAttribute("user")) {
            // This will be populated by flash attributes
        } else if (userId != null) {
            User user = adminDataService.getUserById(userId);  // Retrieves the user by ID
            model.addAttribute("user", user);  // Adds the user to the model
        } else {
            model.addAttribute("user", new User());  // Adds a new User object to the model
        }

        return "modifydata";  // Returns the view name for the modify data page
    }

    @PostMapping("/modifydata")  // Handles POST requests for /admin/datas/modifydata
    public String showModifyDataFormPost(@RequestParam Long userId, Model model) {
        return showModifyDataForm(userId, model);  // Forwards the request to the showModifyDataForm method
    }

    @PostMapping("/updateUser")  // Handles POST requests for /admin/datas/updateUser
    public String updateUser(@ModelAttribute User userFormData, RedirectAttributes redirectAttributes) {
        if (adminDataService.isDuplicateTaxNumber(userFormData)) {  // Checks for duplicate tax number
            redirectAttributes.addFlashAttribute("errorMessage", "Egy felhasználó már rendelkezik a megadott adószámmal!");
            redirectAttributes.addFlashAttribute("user", userFormData);
            return "redirect:/admin/datas/modifydata";  // Redirects to the modify data page if there's a duplicate tax number
        }

        adminDataService.updateUser(userFormData);  // Updates the user data
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres adatmódosítás!");
        redirectAttributes.addFlashAttribute("user", userFormData);
        return "redirect:/admin/datas/modifydata";  // Redirects to the modify data page after successful update
    }

    @GetMapping("/getUserDetailsById/{id}")  // Handles GET requests for /admin/datas/getUserDetailsById/{id}
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public User getUserDetailsById(@PathVariable Long id) {
        return adminDataService.getUserById(id);  // Returns the user details by ID
    }
}