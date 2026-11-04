package com.college.placement.controller;

import com.college.placement.dto.LoggedInUser;
import com.college.placement.service.AuthService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Sign in and sign out.
 *
 * The login page has two tabs and posts a loginType of either "staff" or
 * "student", so the right table is checked.
 */
@Controller
public class AuthController {

    private static final String TYPE_STUDENT = "student";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(defaultValue = "staff") String tab,
                                HttpSession session, Model model) {

        LoggedInUser user = (LoggedInUser) session.getAttribute("user");
        if (user != null) {
            return landingPageFor(user);           // already signed in
        }
        model.addAttribute("loginType", tab);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam(defaultValue = "staff") String loginType,
                          @RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        Optional<LoggedInUser> user = TYPE_STUDENT.equals(loginType)
                ? authService.loginStudent(username, password)
                : authService.loginStaff(username, password);

        if (user.isEmpty()) {
            // one message for both cases, so nobody can find out which user ids exist
            model.addAttribute("errorMessage", "Invalid user id or password.");
            model.addAttribute("username", username);
            model.addAttribute("loginType", loginType);
            return "login";
        }

        session.setAttribute("user", user.get());
        return landingPageFor(user.get());
    }

    @GetMapping("/logout")
    public String doLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?signedOut=true";
    }

    /** Officer and faculty start on the dashboard, a student on their own page. */
    private String landingPageFor(LoggedInUser user) {
        return user.isStudent() ? "redirect:/my-profile" : "redirect:/";
    }
}
