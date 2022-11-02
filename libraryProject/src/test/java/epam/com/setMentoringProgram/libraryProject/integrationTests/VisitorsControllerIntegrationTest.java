package epam.com.setMentoringProgram.libraryProject.integrationTests;

import epam.com.setMentoringProgram.libraryProject.BaseVisitorAbstractTest;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.repositories.VisitorRepository;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.VISITOR_BY_ID_NOT_FOUND;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.getJsonFromObject;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormatAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-integration-test.properties")
@SqlGroup({
        @Sql(value = {"classpath:h2FillOutDBWithData.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:h2ClearFixturesCommand.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class VisitorsControllerIntegrationTest extends BaseVisitorAbstractTest {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getVisitorsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingVisitors()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getVisitorsViaPaginationTest() throws Exception {
        int page = 1;
        int countOfItems = 1;
        Visitor lastVisitorFromDB = visitorRepository.findAll().stream().reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting visitors from h2 DB"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingVisitors())
                        .param("page", String.valueOf(page))
                        .param("countOfItems", String.valueOf(countOfItems)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(lastVisitorFromDB.getId())))
                .andExpect(jsonPath("$.[0].age", is(lastVisitorFromDB.getAge())))
                .andExpect(jsonPath("$.[0].email", is(lastVisitorFromDB.getEmail())))
                .andExpect(jsonPath("$.[0].initials", is(lastVisitorFromDB.getInitials())))
                .andExpect(jsonPath("$.[0].dateOfBirth", is(getDateBySpecificFormatAsString(VISITOR_DATE_VALUES.getDateCreatingPattern(), lastVisitorFromDB.getDateOfBirth()))));
    }

    @Test
    void getVisitorByIdTest() throws Exception {
        Visitor visitorFromDBThatIsGonnaBeGot = visitorRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting visitors from h2 DB"));
        int visitorIdThatIsGonnaBeGotFromBD = visitorFromDBThatIsGonnaBeGot.getId();

        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingVisitorById(visitorIdThatIsGonnaBeGotFromBD)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(visitorIdThatIsGonnaBeGotFromBD)))
                .andExpect(jsonPath("$.age", is(visitorFromDBThatIsGonnaBeGot.getAge())))
                .andExpect(jsonPath("$.email", is(visitorFromDBThatIsGonnaBeGot.getEmail())))
                .andExpect(jsonPath("$.initials", is(visitorFromDBThatIsGonnaBeGot.getInitials())))
                .andExpect(jsonPath("$.dateOfBirth", is(getDateBySpecificFormatAsString(VISITOR_DATE_VALUES.getDateCreatingPattern(), visitorFromDBThatIsGonnaBeGot.getDateOfBirth()))));
    }

    @Test
    void createVisitorTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(getLinkForCreatingVisitor())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(williamVisitor.setId(null))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));

        assertThat(visitorRepository.findByInitialsAndDateOfBirth(WILLIAM_INITIALS, WILLIAM_DATE_OF_BIRTH).setBooks(null))
                .isNotNull()
                .hasFieldOrPropertyWithValue("age", WILLIAM_AGE)
                .hasFieldOrPropertyWithValue("email", WILLIAM_EMAIL);
    }

    @Test
    void updateVisitorTest() throws Exception {
        int visitorIdThatIsGonnaBeUpdated = visitorRepository.findAll().stream().reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting visitors from h2 DB")).getId();

        Visitor newVisitorForUpdating = new Visitor().setInitials("Norma Jeane Morten").setAge(57).setEmail("merlinMonro@gmail.com")
                .setDateOfBirth(getDateBySpecificFormat(VISITOR_DATE_VALUES, "17-06-1965"));

        mockMvc.perform(MockMvcRequestBuilders
                        .put(getLinkForUpdatingVisitor(visitorIdThatIsGonnaBeUpdated))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(newVisitorForUpdating)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(visitorIdThatIsGonnaBeUpdated)))
                .andExpect(jsonPath("$.age", is(newVisitorForUpdating.getAge())))
                .andExpect(jsonPath("$.email", is(newVisitorForUpdating.getEmail())))
                .andExpect(jsonPath("$.initials", is(newVisitorForUpdating.getInitials())))
                .andExpect(jsonPath("$.dateOfBirth", is(getDateBySpecificFormatAsString(VISITOR_DATE_VALUES.getDateCreatingPattern(), newVisitorForUpdating.getDateOfBirth()))));

        assertThat(visitorRepository.findById(visitorIdThatIsGonnaBeUpdated).orElseThrow(() -> new EntityNotFoundException(VISITOR_BY_ID_NOT_FOUND.getErrorMessage())))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", visitorIdThatIsGonnaBeUpdated)
                .hasFieldOrPropertyWithValue("age", newVisitorForUpdating.getAge())
                .hasFieldOrPropertyWithValue("dateOfBirth", newVisitorForUpdating.getDateOfBirth());
    }

    @Test
    void deleteVisitorTest() throws Exception {
        int visitorIdThatIsGonnaBeDeleted = visitorRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting visitors from h2 DB")).getId();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(getLinkForDeletingVisitor(visitorIdThatIsGonnaBeDeleted)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        List<Visitor> visitorsFromDBAfterDeletingOneVisitor = visitorRepository.findAll();

        assertThat(visitorsFromDBAfterDeletingOneVisitor).isNotNull().hasSize(1);
        assertThat(visitorsFromDBAfterDeletingOneVisitor.get(0).getId()).isNotEqualTo(visitorIdThatIsGonnaBeDeleted);
    }

}