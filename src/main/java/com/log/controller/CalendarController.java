package com.log.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.log.model.DailyLog;
import com.log.model.User;
import com.log.repository.DailyLogRepository;
import com.log.repository.UserRepository;
import jakarta.servlet.http.HttpSession;  // Correct import


@Controller
public class CalendarController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DailyLogRepository dailyLogRepository;
    
    @GetMapping("/calendar")
    public String showCalendar(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DailyLog> logs = dailyLogRepository.findByUserIdOrderByDateDesc(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("logs", logs);
        model.addAttribute("today", LocalDate.now());
        
        return "calendar";
    }
    
//    @PostMapping("/calendar")
//    public String saveDailyLog(
//            HttpSession session,
//            @RequestParam(required = false) boolean done,
//            @RequestParam int timeInvested,
//            @RequestParam String selectedDate) {
//        
//        Long userId = (Long) session.getAttribute("userId");
//        
//        if (userId == null) {
//            return "redirect:/login";
//        }
//        
//        LocalDate logDate = LocalDate.parse(selectedDate);
//        DailyLog log = dailyLogRepository.findByUserIdAndDate(userId, logDate)
//            .orElse(new DailyLog());
//        
//        log.setUserId(userId);
//        log.setDate(logDate);
//        log.setDone(done);
//        log.setTimeInvested(timeInvested);
//        
//        dailyLogRepository.save(log);
//        
//        return "redirect:/calendar";
//    }
    
    @PostMapping("/calendar")
    public String saveDailyLog(
            HttpSession session,
            @RequestParam(required = false) boolean done,
            @RequestParam int timeInvested,
            @RequestParam String selectedDate,
            @RequestParam(required = false) String message) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        LocalDate logDate = LocalDate.parse(selectedDate);
        DailyLog log = dailyLogRepository.findByUserIdAndDate(userId, logDate)
            .orElse(new DailyLog());

        log.setUserId(userId);
        log.setDate(logDate);
        log.setDone(done);
        log.setTimeInvested(timeInvested);
        log.setMessage(message);   // NEW

        dailyLogRepository.save(log);

        return "redirect:/calendar";
    }

}
