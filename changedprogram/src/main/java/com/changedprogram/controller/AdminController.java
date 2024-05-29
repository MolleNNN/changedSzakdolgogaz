package com.changedprogram.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.EncryptionUtil;
import com.changedprogram.entity.Company;
import com.changedprogram.entity.Position;
import com.changedprogram.entity.Receiver;
import com.changedprogram.entity.SignatureDTO;
import com.changedprogram.entity.UploadResult;
import com.changedprogram.entity.User;
import com.changedprogram.entity.UserDTO;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.ReceiverRepository;
import com.changedprogram.repository.UserRepository;
import com.changedprogram.service.AdminService;
import com.changedprogram.service.FileService;

import jakarta.servlet.http.HttpServletRequest;
@Controller
public class AdminController {




    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ReceiverRepository receiverRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EncryptionUtil encryptionUtil;
    @Autowired
    private AdminService adminService;

    
    @GetMapping("/admin")
    public String adminPage(Model model) {
        long activeUsers = userRepository.countByActive(true);
        long inactiveUsers = userRepository.countByActive(false);

        Map<String, Long> stats = new HashMap<>();
        stats.put("active", activeUsers);
        stats.put("inactive", inactiveUsers);

        model.addAttribute("userStats", stats);

        return "admin"; // points to src/main/resources/templates/admin.html
    }
    
    @GetMapping("/admin/user-stats")
    @ResponseBody
    public Map<String, Long> getUserStats() {
        long activeUsers = userRepository.countByActive(true);
        long inactiveUsers = userRepository.countByActive(false);

        Map<String, Long> stats = new HashMap<>();
        stats.put("active", activeUsers);
        stats.put("inactive", inactiveUsers);

        return stats;
    }
    
    
    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        return "login";
    }
 
    @GetMapping("/admin/data")
    public String displayUserData(@RequestParam(defaultValue = "name") String sortBy,
                                  @RequestParam(defaultValue = "asc") String order,
                                  Model model) {
        List<User> users = adminService.getAllUserDataSorted(sortBy, order);
        List<UserDTO> userDTOs = users.stream().map(user -> {
            List<SignatureDTO> signatures = user.getResults().stream().map(result -> {
                String decryptedSignature = null;
                if (result.getSignature() != null) {
                    try {
                        byte[] decodedSignature = encryptionUtil.decodeBase64(result.getSignatureBase64());
                        decryptedSignature = encryptionUtil.decrypt(decodedSignature);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new SignatureDTO(result.getPpt().getFilename(), decryptedSignature, result.getValid(), result.getFilled());
            }).collect(Collectors.toList());
            return new UserDTO(user, signatures, user.getBirthdate());
        }).collect(Collectors.toList());
        model.addAttribute("users", userDTOs);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);
        return "data";
    }


 

}

