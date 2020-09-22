package com.lambda.watermyplants.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambda.watermyplants.models.Plant;
import com.lambda.watermyplants.models.Role;
import com.lambda.watermyplants.models.User;
import com.lambda.watermyplants.models.UserRoles;
import com.lambda.watermyplants.services.PlantService;
import com.lambda.watermyplants.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class PlantContollerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private PlantService plantService;

    @MockBean
    private UserService userService;

    private List<Plant> plantList = new ArrayList<>();

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
        u1.setUserid(65);

        User u2 = new User("chris", "chris@watermyplants.com", "8675309", "taco");
        u2.getRoles().add(new UserRoles(u2, r2));
        u2.setUserid(70);

        Plant p1 = new Plant(u1, "precious", "aloe", "", 48);
        p1.setPlantid(1);
        Plant p2 = new Plant(u2, "sunny", "flower", "", 18);
        p2.setPlantid(2);
        plantList.add(p1);
        plantList.add(p2);
        userList.add(u1);
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
    public void getAllPlants() throws Exception {
        String apiUrl = "/plants/plants";
        Mockito.when(plantService.findAll()).thenReturn(plantList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(plantList);

        assertEquals(er,tr);


    }

    @Test
    public void getPlantById() throws Exception {
        String apiUrl = "/plants/plant/1";
        Mockito.when(plantService.findPlantById(1)).thenReturn(plantList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(plantList.get(0));

        assertEquals(er,tr);


    }

    @Test
    @WithMockUser(username = "chris")
    public void getUserPlants() throws Exception {
        String apiUrl = "/plants/user/plants";
        Mockito.when(userService.findByName("chris")).thenReturn(userList.get(1));
        Mockito.when(plantService.findByUserId(70)).thenReturn(plantList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(plantList);

        assertEquals(er,tr);

    }

    @Test
    public void addPlant() throws Exception {
        String apiUrl = "/plants/plant";
        Plant newPlant = new Plant();
        newPlant.setUser(userList.get(1));
        newPlant.setNickname("test");
        newPlant.setSpecies("thorn");
        newPlant.setFrequency(4);
        newPlant.setImage("someImage");
        newPlant.setPlantid(0);


        ObjectMapper mapper = new ObjectMapper();
        String plantString = mapper.writeValueAsString(newPlant);

        Mockito.when(plantService.save(eq(70),any(Plant.class))).thenReturn(newPlant);

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(plantString);
        mockMvc.perform(rb)
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void addNewUserPlant() {
    }

    @Test
    public void replacePlant() {
    }

    @Test
    public void replaceUserPlant() {
    }

    @Test
    public void editUserPlant() {
    }

    @Test
    public void removeUserPlant() {
    }
}