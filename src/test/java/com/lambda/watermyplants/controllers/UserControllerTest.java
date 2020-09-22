package com.lambda.watermyplants.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambda.watermyplants.models.Plant;
import com.lambda.watermyplants.models.Role;
import com.lambda.watermyplants.models.User;
import com.lambda.watermyplants.models.UserRoles;
import com.lambda.watermyplants.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private List<User> userList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        Role r1 = new Role("admin");
        r1.setRoleid(1);
        Role r2 = new Role("user");
        r2.setRoleid(2);

        User u1 = new User("admin", "admin@watermyplants.com", "8675309", "password");
        u1.getRoles().add(new UserRoles(u1, r1));
        u1.getRoles().add(new UserRoles(u1, r2));

        u1.getUserplants().add(new Plant(u1, "precious", "aloe", "", 48));
        u1.getUserplants().add(new Plant(u1, "pokey", "cactus", "", 96));

        u1.getUserplants().get(0).setPlantid(10);
        u1.getUserplants().get(1).setPlantid(11);
        u1.setUserid(55);

        userList.add(u1);

        User u2 = new User("chris", "chris@watermyplants.com", "8675309", "taco");
        u2.getRoles().add(new UserRoles(u2, r2));

        u2.getUserplants().add(new Plant(u2, "sunny", "flower", "", 18));

        u2.getUserplants().get(0).setPlantid(15);
        u2.setUserid(60);

        userList.add(u2);

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listAllUsers() throws Exception {
        String apiUrl = "/users/users";
        Mockito.when(userService.findAll()).thenReturn(userList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList);

        assertEquals(er,tr);
    }

    @Test
    public void getUserByName() throws Exception {
        String apiUrl = "/users/name/testing";
        Mockito.when(userService.findByName("testing")).thenReturn(userList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(0));

        assertEquals(er,tr);
    }

    @Test
    public void getUserByLikeName() throws Exception {
        String apiUrl = "/users/name/like/testing";
        Mockito.when(userService.findByNameContaining(any(String.class))).thenReturn(userList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList);

        assertEquals(er,tr);
    }
    @Test
    @WithMockUser(username = "chris")
    public void getUserByAuthenticatedName() throws Exception {
        String apiUrl = "/users/user";
        Mockito.when(userService.findByName("chris")).thenReturn(userList.get(1));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(1));

        assertEquals(er,tr);
    }

    @Test
    public void addNewUser() throws Exception {
        String apiUrl = "/users/user";
        User u1 = new User();
        u1.setUserid(155);
        u1.setUsername("hanina");
        u1.setPassword("plants");
        u1.setEmail("hanina@school.com");

        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(u1);

        Mockito.when(userService.save(any(User.class))).thenReturn(u1);

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(userString);
        mockMvc.perform(rb)
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateFullUser() throws Exception {
        String apiUrl = "/users/user/0";

        User u1 = new User();
        u1.setUserid(155);
        u1.setUsername("hanina");
        u1.setPassword("plants");
        u1.setEmail("hanina@school.com");

        Mockito.when(userService.update(u1, 100)).thenReturn(userList.get(0));

        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(u1);

        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl, 100)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(userString);
        mockMvc.perform(rb)
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @WithMockUser(username = "chris")
    public void updateUser() throws Exception{
        String apiUrl = "/users/user";

        User u1 = new User();
        u1.setUserid(1);
        u1.setUsername("chris");
        u1.setEmail("hanina@school.com");
        Mockito.when(userService.findByName("chris")).thenReturn(u1);
        Mockito.when(userService.update(u1, 1)).thenReturn(userList.get(1));

        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(u1);
        System.out.println(userString);

        RequestBuilder rb = MockMvcRequestBuilders.patch(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(userString);
        mockMvc.perform(rb)
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(username = "chris")
    public void deleteUser() throws Exception{
        String apiUrl = "/users/user";

        User u1 = new User();
        u1.setUserid(1);
        u1.setUsername("chris");
        u1.setEmail("hanina@school.com");
        Mockito.when(userService.findByName("chris")).thenReturn(u1);


        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(rb)
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void adminUserDelete() throws Exception {
        String apiUrl = "/users/user/0";

        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(rb)
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());

    }
}