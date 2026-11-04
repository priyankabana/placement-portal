package com.college.placement.config;

import com.college.placement.dto.LoggedInUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.regex.Pattern;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Runs before every controller and decides who may open the page.
 *
 *   no session -> sent to the login page
 *   STAFF      -> everything is allowed (officer and faculty)
 *   STUDENT    -> only the pages listed below
 *
 * For a student the allowed URLs are matched with a pattern instead of a simple
 * "starts with" check. Otherwise /companies would also let /companies/new and
 * /companies/save through, and a student could add a record.
 */
public class AuthInterceptor implements HandlerInterceptor {

    /** Pages a student may open with GET. */
    private static final Pattern STUDENT_PAGES =
            Pattern.compile("^/(my-profile|logout|companies|drives|companies/\\d+|drives/\\d+)$");

    /** The only thing a student may post - applying to a drive. */
    private static final Pattern STUDENT_ACTIONS = Pattern.compile("^/my-applications/apply$");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        LoggedInUser user = session == null ? null : (LoggedInUser) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (user.isStaff()) {
            return true;
        }

        String path = request.getRequestURI();
        boolean allowed = "POST".equalsIgnoreCase(request.getMethod())
                ? STUDENT_ACTIONS.matcher(path).matches()
                : STUDENT_PAGES.matcher(path).matches();

        if (allowed) {
            return true;
        }

        // a student asked for an officer page - send them back to their own page
        response.sendRedirect(request.getContextPath() + "/my-profile");
        return false;
    }
}
