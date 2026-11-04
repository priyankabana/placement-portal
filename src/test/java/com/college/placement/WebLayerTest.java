package com.college.placement;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.college.placement.dto.LoggedInUser;
import com.college.placement.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * HTTP level checks: health check, JSON API and the sign in guard.
 *
 * MockMvc is built from the same application context as the other test classes.
 * A second context would run data.sql again on the shared in memory database.
 */
@SpringBootTest
@Transactional
class WebLayerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("health endpoint is open and reports UP")
    void healthIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("GET /api/stats returns the placement numbers of the sample season")
    void statsApi() throws Exception {
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(20))
                .andExpect(jsonPath("$.placedStudents").value(8))
                .andExpect(jsonPath("$.placementPercentage").value(40.0))
                .andExpect(jsonPath("$.highestPackageLpa").value(7.0))
                .andExpect(jsonPath("$.applicationsByStage.SELECTED").value(8))
                .andExpect(jsonPath("$.branches", hasSize(5)));
    }

    @Test
    @DisplayName("GET /api/drives lists every drive and filters by status")
    void drivesApi() throws Exception {
        mockMvc.perform(get("/api/drives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].company").isString());

        mockMvc.perform(get("/api/drives").param("status", "OPEN"))
                .andExpect(jsonPath("$", hasSize(4)));

        mockMvc.perform(get("/api/drives").param("status", "DONE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    @DisplayName("pages need a sign in, and a student cannot open officer pages")
    void signInGuard() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(redirectedUrl("/login"));

        MockHttpSession studentSession = new MockHttpSession();
        studentSession.setAttribute("user",
                new LoggedInUser(UserRole.STUDENT, 1L, "Aditi Kulkarni", "CS22001", "CSE"));
        mockMvc.perform(get("/students").session(studentSession))
                .andExpect(redirectedUrl("/my-profile"));
        mockMvc.perform(get("/drives").session(studentSession))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("staff sign in with the seeded account lands on the dashboard")
    void staffSignIn() throws Exception {
        mockMvc.perform(post("/login")
                        .param("loginType", "staff")
                        .param("username", "placement.officer")
                        .param("password", "officer@123"))
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(post("/login")
                        .param("loginType", "staff")
                        .param("username", "placement.officer")
                        .param("password", "wrong"))
                .andExpect(status().isOk());   // stays on the login page with a message
    }
}
