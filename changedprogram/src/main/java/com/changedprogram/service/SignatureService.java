package com.changedprogram.service;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.component.EncryptionUtil;
import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.Result;
import com.changedprogram.entity.Type;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.ResultRepository;
import com.changedprogram.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class SignatureService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PptRepository pptRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Transactional
    public String finalizeSignature(Long userId, Long pptId, String signatureData, HttpSession session, RedirectAttributes redirectAttributes) {
        Result result = resultRepository.findByUserIdAndPptId(userId, pptId)
            .orElseGet(() -> {
                Result newResult = new Result();
                newResult.setUser(userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Felhasználó nem található")));
                newResult.setPpt(pptRepository.findById(pptId).orElseThrow(() -> new IllegalArgumentException("Ppt nem található")));
                newResult.setFilled(new Date());
                newResult.setNotificationSent(false);
                return newResult;
            });

        if (signatureData != null && signatureData.startsWith("data:image/png;base64,")) {
            signatureData = signatureData.substring("data:image/png;base64,".length());
        }

        try {
            String encryptedSignature = encryptionUtil.encrypt(signatureData);
            result.setSignature(Base64.getDecoder().decode(encryptedSignature));

            // Calculate valid date based on the Type's valid field in months
            Ppt ppt = result.getPpt();
            Type type = ppt.getType();
            int validMonths = type.getValid();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, validMonths);
            result.setValid(calendar.getTime());

            resultRepository.save(result);
            return "redirect:/completion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hiba az aláírás feldolgozása közben");
            System.err.println("Hiba az aláírás feldolgozása közben: " + e.getMessage());
            return "redirect:/error";
        }
    }
}