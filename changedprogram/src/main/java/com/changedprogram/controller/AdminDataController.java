package com.changedprogram.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.changedprogram.EncryptionUtil;
import com.changedprogram.dto.ResultDTO;
import com.changedprogram.dto.UserrDTO;
import com.changedprogram.entity.Result;
import com.changedprogram.entity.User;
import com.changedprogram.repository.ResultRepository;
import com.changedprogram.repository.UserRepository;
import com.changedprogram.service.AdminDataService;
/*
@Controller
public class AdminDataController {

    @Autowired
    private AdminDataService adminDataService;

    @GetMapping("/admin/datas")
    public String viewUserData(Model model) {
        return "adminDatas"; // Points to src/main/resources/templates/adminDatas.html
    }

    @GetMapping("/admin/datas/data")
    @ResponseBody
    public ResponseEntity<List<UserrDTO>> getUserData() {
        List<UserrDTO> users = adminDataService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}*/

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
 /*   
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
                .map(result -> new ResultDTO(
                        result.getId(),
                        result.getFilled(),
                        result.isNotificationSent(),
                        result.getValid(),
                        result.getPpt() != null ? result.getPpt().getFilename() : null))
                .collect(Collectors.toList());
    }

    @GetMapping("/pptdetails/{pptId}")
    @ResponseBody
    public ResultDTO getPptDetails(@PathVariable Long pptId) {
        Result result = resultRepository.findById(pptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found"));
        return new ResultDTO(
                result.getId(),
                result.getFilled(),
                result.isNotificationSent(),
                result.getValid(),
                result.getPpt() != null ? result.getPpt().getFilename() : null);
    }
    
    @GetMapping
    public String getAdminDatasPage(Model model) {
        return "adminDatas";  // This should match the name of your Thymeleaf template (adminDatas.html)
    }
}
*/
    
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
                            decryptedSignature
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
                decryptedSignature
        );
    }

    @GetMapping
    public String getAdminDatasPage(Model model) {
        return "adminDatas";  // This should match the name of your Thymeleaf template (adminDatas.html)
    }
}