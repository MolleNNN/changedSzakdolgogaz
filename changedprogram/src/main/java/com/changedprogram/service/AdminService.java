package com.changedprogram.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.User;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PptRepository pptRepository;

    public List<User> getAllUserDataSorted(String sortBy, String order) {
        List<User> users = userRepository.findAllWithDetails(sortBy);
        users.sort((u1, u2) -> {
            int result = 0;
            switch (sortBy) {
                case "results.filled":
                    result = compareDates(u1, u2, order, true);
                    if (result == 0) {
                        result = u1.getResults().stream().findFirst().get().getPpt().getFilename()
                                .compareTo(u2.getResults().stream().findFirst().get().getPpt().getFilename());
                    }
                    if (result == 0) {
                        result = u1.getName().compareTo(u2.getName());
                    }
                    break;
                case "results.valid":
                    result = compareDates(u1, u2, order, false);
                    if (result == 0) {
                        result = u1.getResults().stream().findFirst().get().getPpt().getFilename()
                                .compareTo(u2.getResults().stream().findFirst().get().getPpt().getFilename());
                    }
                    if (result == 0) {
                        result = u1.getName().compareTo(u2.getName());
                    }
                    break;
                default:
                    result = compareFields(u1, u2, sortBy, order);
                    break;
            }
            return result;
        });
        return users;
    }

    private int compareDates(User u1, User u2, String order, boolean isFilledDate) {
        Date date1 = isFilledDate ? u1.getResults().stream().findFirst().get().getFilled() :
                                    u1.getResults().stream().findFirst().get().getValid();
        Date date2 = isFilledDate ? u2.getResults().stream().findFirst().get().getFilled() :
                                    u2.getResults().stream().findFirst().get().getValid();
        return "asc".equalsIgnoreCase(order) ? date1.compareTo(date2) : date2.compareTo(date1);
    }

    private int compareFields(User u1, User u2, String sortBy, String order) {
        Comparator<User> comparator;
        switch (sortBy) {
            case "name":
                comparator = Comparator.comparing(User::getName);
                break;
            case "birthdate":
                comparator = Comparator.comparing(User::getBirthdate);
                break;
            case "position.name":
                comparator = Comparator.comparing(u -> u.getPosition().getName());
                break;
            case "company.name":
                comparator = Comparator.comparing(u -> u.getCompany().getName());
                break;
            case "receiver.name":
                comparator = Comparator.comparing(u -> u.getReceiver().getName());
                break;
            case "active":
                comparator = Comparator.comparing(User::isActive);
                break;
            default:
                comparator = Comparator.comparing(User::getName);
                break;
        }
        return "asc".equalsIgnoreCase(order) ? comparator.compare(u1, u2) : comparator.reversed().compare(u1, u2);
    }
    
    @Transactional
    public void setActivePpts(List<Long> pptIds) {
        // Deactivate all PPTs first
        List<Ppt> allPpts = pptRepository.findAll();
        allPpts.forEach(ppt -> ppt.setActive(false));

        // Set the selected PPTs to active
        List<Ppt> activePpts = pptRepository.findAllById(pptIds);
        activePpts.forEach(ppt -> ppt.setActive(true));

        // Save changes to the database
        pptRepository.saveAll(allPpts);
    }
}