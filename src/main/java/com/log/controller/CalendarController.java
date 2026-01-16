package com.log.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    
//    @GetMapping("/calendar")
//    public String showCalendar(HttpSession session, Model model) {
//        Long userId = (Long) session.getAttribute("userId");
//        
//        if (userId == null) {
//            return "redirect:/login";
//        }
//        
//        User user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//        
//        List<DailyLog> logs = dailyLogRepository.findByUserIdOrderByDateDesc(user.getId());
//        
//        model.addAttribute("user", user);
//        model.addAttribute("logs", logs);
//        model.addAttribute("today", LocalDate.now());
//        
//        return "calendar";
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
    
    @GetMapping("/calendar")
    public String showCalendar(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page) {  // page index, 0-based

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        int pageSize = 7; // 7 logs per page (roughly 1 week)
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<DailyLog> logPage = dailyLogRepository
                .findByUserIdOrderByDateDesc(user.getId(), pageable);

        model.addAttribute("user", user);
        model.addAttribute("logs", logPage.getContent());   // current 7 logs
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());
        model.addAttribute("today", LocalDate.now());

        // For calendar highlighting: send all dates that have logs (only dates)
        List<LocalDate> allLogDates =
                dailyLogRepository.findAll()
                        .stream()
                        .map(DailyLog::getDate)
                        .distinct()
                        .toList();
        model.addAttribute("logDates", allLogDates);

        return "calendar";
    }


}
