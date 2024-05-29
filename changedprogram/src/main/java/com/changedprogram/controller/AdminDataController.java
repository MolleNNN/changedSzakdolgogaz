package com.changedprogram.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.EncryptionUtil;
import com.changedprogram.dto.ResultDTO;
import com.changedprogram.dto.UserrDTO;
import com.changedprogram.entity.Result;
import com.changedprogram.entity.User;
import com.changedprogram.repository.ResultRepository;
import com.changedprogram.repository.UserRepository;
import com.changedprogram.service.AdminDataService;


@Controller
@RequestMapping("/admin/datas")
public class AdminDataController {

    @Autowired
    private AdminDataService adminDataService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private EncryptionUtil encryptionUtil;

    
    @GetMapping("/data")
    @ResponseBody
    public List<UserrDTO> getAllUsers() {
        return adminDataService.getAllUsers();
    }

    @GetMapping("/results/{userId}")
    @ResponseBody
    public List<ResultDTO> getResultsByUserId(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return user.getResults().stream()
                .map(result -> {
                    String decryptedSignature = "";
                    if (result.getSignature() != null) {
                        try {
                            decryptedSignature = encryptionUtil.decrypt(result.getSignature());
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
                .collect(Collectors.toList());
    }

    @GetMapping("/pptdetails/{pptId}")
    @ResponseBody
    public ResultDTO getPptDetails(@PathVariable Long pptId) {
        Result result = resultRepository.findById(pptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found"));
        String decryptedSignature = "";
        if (result.getSignature() != null) {
            try {
                decryptedSignature = encryptionUtil.decrypt(result.getSignature());
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

    @GetMapping
    public String getAdminDatasPage(Model model) {
        return "adminDatas";  // This should match the name of your Thymeleaf template (adminDatas.html)
    }
    
    
    // Modify data
    @GetMapping("/modifydata")
    public String showModifyDataForm(@RequestParam(required = false) Long userId, Model model) {
        model.addAttribute("users", adminDataService.getAllUsers());
        model.addAttribute("positions", adminDataService.getAllPositions());
        model.addAttribute("companies", adminDataService.getAllCompanies());
        model.addAttribute("receivers", adminDataService.getAllReceivers());

        if (model.containsAttribute("user")) {
            // This will be populated by flash attributes
        } else if (userId != null) {
            User user = adminDataService.getUserById(userId);
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", new User());
        }

        return "modifydata";
    }

    @PostMapping("/modifydata")
    public String showModifyDataFormPost(@RequestParam Long userId, Model model) {
        return showModifyDataForm(userId, model);
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute User userFormData, RedirectAttributes redirectAttributes) {
        if (adminDataService.isDuplicateTaxNumber(userFormData)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Egy felhasználó már rendelkezik a megadott adószámmal!");
            redirectAttributes.addFlashAttribute("user", userFormData);
            return "redirect:/admin/datas/modifydata";
        }

        adminDataService.updateUser(userFormData);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres adatmódosítás!");
        redirectAttributes.addFlashAttribute("user", userFormData);
        return "redirect:/admin/datas/modifydata";
    }

    @GetMapping("/getUserDetailsById/{id}")
    @ResponseBody
    public User getUserDetailsById(@PathVariable Long id) {
        return adminDataService.getUserById(id); // Add error handling as appropriate
    }
}