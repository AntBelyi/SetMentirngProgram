package epam.com.setMentoringProgram.libraryProject.controllers;

import epam.com.setMentoringProgram.libraryProject.BaseVisitorAbstractTest;
import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.validators.VisitorValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.getJsonFromObject;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormatAsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VisitorsController.class)
class VisitorsControllerTest extends BaseVisitorAbstractTest {

    @MockBean
    private VisitorService visitorService;

    @MockBean
    private VisitorValidator visitorValidator;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final List<VisitorDto> initVisitorDtoList = initVisitorList.stream()
            .map(visitor -> convertToEntity(visitor, VisitorDto.class)).collect(Collectors.toList());

    private static final VisitorDto steveVisitorDto = convertToEntity(steveVisitor, VisitorDto.class);
    private static final VisitorDto williamVisitorDto = convertToEntity(williamVisitor, VisitorDto.class);

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void getVisitors() throws Exception {
        when(visitorService.getVisitors(VisitorDto.class)).thenReturn(initVisitorDtoList);

        final String link = "/visitors";
        mockMvc.perform(MockMvcRequestBuilders
                        .get(link))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(initVisitorDtoList.size())))
                .andExpectAll(jsonPath("$.[0].id", is(STEVE_ID)));

        verify(visitorService, times(1)).getVisitors(VisitorDto.class);
    }

    @Test
    void getVisitorsWithPaginationTest() throws Exception {
        int page = 1;
        int countOfItems = 2;
        when(visitorService.getVisitors(page, countOfItems, VisitorDto.class)).thenReturn(initVisitorDtoList);

        final String link = String.format("/visitors?page=%d&countOfItems=%d", page, countOfItems);

        mockMvc.perform(MockMvcRequestBuilders
                .get(link)
                .param("page", String.valueOf(page))
                .param("countOfItems", String.valueOf(countOfItems)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(initVisitorDtoList.size())))
                .andExpectAll(jsonPath("$.[0].id", is(STEVE_ID)))
                .andExpectAll(jsonPath("$.[1].id", is(WILLIAM_ID)));

        verify(visitorService, times(1)).getVisitors(page, countOfItems, VisitorDto.class);
    }

    @Test
    void getVisitorById() throws Exception {
        when(visitorService.getVisitorById(anyInt())).thenReturn(convertToEntity(williamVisitorDto, Visitor.class));

        final String link = String.format("/visitors/%s", WILLIAM_ID);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(link))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(WILLIAM_ID)))
                .andExpect(jsonPath("$.age", is(WILLIAM_AGE)))
                .andExpect(jsonPath("$.email", is(WILLIAM_EMAIL)))
                .andExpect(jsonPath("$.initials", is(WILLIAM_INITIALS)))
                .andExpect(jsonPath("$.dateOfBirth", is(getDateBySpecificFormatAsString(VISITOR_DATE_VALUES.getDateCreatingPattern(), WILLIAM_DATE_OF_BIRTH))));

        verify(visitorService, times(1)).getVisitorById(anyInt());
    }

    @Test
    void createVisitor() throws Exception {
        when(visitorService.createVisitor(williamVisitorDto)).thenReturn(initVisitorDtoList);

        final String link = "/visitors/new";

        mockMvc.perform(MockMvcRequestBuilders
                        .post(link)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(williamVisitorDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(initVisitorDtoList.size())))
                .andExpect(jsonPath("$.[1].id", is(WILLIAM_ID)))
                .andExpect(jsonPath("$.[1].age", is(WILLIAM_AGE)))
                .andExpect(jsonPath("$.[1].email", is(WILLIAM_EMAIL)))
                .andExpect(jsonPath("$.[1].initials", is(WILLIAM_INITIALS)))
                .andExpect(jsonPath("$.[1].dateOfBirth", is(getDateBySpecificFormatAsString(VISITOR_DATE_VALUES.getDateCreatingPattern(), WILLIAM_DATE_OF_BIRTH))));
    }

    @Test
    void updateVisitor() throws Exception {
        VisitorDto updatedVisitorDto = new VisitorDto().setAge(35).setInitials("William Jefferson Clinton").setEmail("clinton@gmail.com")
                        .setDateOfBirth(getDateBySpecificFormat(VISITOR_DATE_VALUES.getDateCreatingPattern(), "23-06-2020"));

        when(visitorService.updatedVisitor(STEVE_ID, updatedVisitorDto)).thenReturn(steveVisitorDto);

        final String link = String.format("/visitors/update/%s", STEVE_ID);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(link)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(updatedVisitorDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(STEVE_ID)))
                .andExpect(jsonPath("$.age", is(STEVE_AGE)))
                .andExpect(jsonPath("$.email", is(STEVE_EMAIL)))
                .andExpect(jsonPath("$.initials", is(STEVE_INITIALS)))
                .andExpect(jsonPath("$.dateOfBirth", is(getDateBySpecificFormatAsString(VISITOR_DATE_VALUES.getDateCreatingPattern(), STEVE_DATE_OF_BIRTH))));

        verify(visitorService, times(1)).updatedVisitor(STEVE_ID, updatedVisitorDto);
    }

    @Test
    void deleteVisitor() throws Exception {
        final String link = String.format("/visitors/delete/%s", STEVE_ID);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(link))
                .andDo(print())
                .andExpect(status().isOk());
    }
}