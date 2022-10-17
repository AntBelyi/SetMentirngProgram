package epam.com.setMentoringProgram.libraryProject.unitTests.services;

import epam.com.setMentoringProgram.libraryProject.BaseVisitorAbstractTest;
import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.repositories.VisitorRepository;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.VISITOR_BY_ID_NOT_FOUND;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest extends BaseVisitorAbstractTest {

    @Mock
    private VisitorRepository visitorRepository;

    @InjectMocks
    private VisitorService visitorService;

    @Test
    void getVisitorsTest() {
        when(visitorRepository.findAll(Sort.by(ID_FIELD))).thenReturn(initVisitorList);

        List<Visitor> visitorList = visitorService.getVisitors();

        assertThat(visitorList).isNotNull().hasSameSizeAs(initVisitorList);

        for (int i = 0; i < visitorList.size(); i++) {
            assertThat(visitorList.get(i).getId()).isEqualTo(initVisitorList.get(i).getId());
            assertThat(visitorList.get(i).getAge()).isEqualTo(initVisitorList.get(i).getAge());
            assertThat(visitorList.get(i).getEmail()).isEqualTo(initVisitorList.get(i).getEmail());
            assertThat(visitorList.get(i).getInitials()).isEqualTo(initVisitorList.get(i).getInitials());
            assertThat(visitorList.get(i).getDateOfBirth()).isEqualTo(initVisitorList.get(i).getDateOfBirth());
        }
    }

    @Test
    void getVisitorsDtoTest() {
        when(visitorRepository.findAll(Sort.by(ID_FIELD))).thenReturn(initVisitorList);

        List<VisitorDto> visitorDtoList = visitorService.getVisitors(VisitorDto.class);

        assertThat(visitorDtoList).isNotNull().hasSameSizeAs(initVisitorList);

        for (int i = 0; i < visitorDtoList.size(); i++) {
            assertThat(visitorDtoList.get(i).getId()).isEqualTo(initVisitorList.get(i).getId());
            assertThat(visitorDtoList.get(i).getAge()).isEqualTo(initVisitorList.get(i).getAge());
            assertThat(visitorDtoList.get(i).getEmail()).isEqualTo(initVisitorList.get(i).getEmail());
            assertThat(visitorDtoList.get(i).getInitials()).isEqualTo(initVisitorList.get(i).getInitials());
            assertThat(visitorDtoList.get(i).getDateOfBirth()).isEqualTo(initVisitorList.get(i).getDateOfBirth());
        }
    }

    @Test
    void getVisitorById() {
        when(visitorRepository.findById(WILLIAM_ID)).thenReturn(Optional.of(williamVisitor));

        Visitor obtainedVisitorById = visitorService.getVisitorById(WILLIAM_ID);

        assertThat(obtainedVisitorById)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID_FIELD, WILLIAM_ID);
    }

    @Test
    void getVisitorByInitials() {
        when(visitorRepository.findByInitialsAndDateOfBirth(STEVE_INITIALS, STEVE_DATE_OF_BIRTH)).thenReturn(steveVisitor);

        Visitor obtainedVisitorByInitials = visitorService.getVisitorByInitialsAndDateOfBirth(STEVE_INITIALS, STEVE_DATE_OF_BIRTH);

        verify(visitorRepository, times(1)).findByInitialsAndDateOfBirth(STEVE_INITIALS, STEVE_DATE_OF_BIRTH);
        assertThat(obtainedVisitorByInitials)
                .isNotNull()
                .hasFieldOrPropertyWithValue("initials", STEVE_INITIALS)
                .hasFieldOrPropertyWithValue("dateOfBirth", STEVE_DATE_OF_BIRTH);
    }

    @Test
    void createVisitorTest() {
        Visitor visitor = mock(Visitor.class);
        visitorService.createVisitor(visitor);
        verify(visitorRepository, times(1)).save(visitor);
    }

    @Test
    void createVisitorDtoTest() {
        VisitorDto visitorDto = mock(VisitorDto.class);

        when(visitorRepository.findAll(Sort.by(ID_FIELD))).thenReturn(initVisitorList);

        List<VisitorDto> visitorDtoList = visitorService.createVisitor(visitorDto);

        verify(visitorRepository, times(1)).save(convertToEntity(visitorDto, Visitor.class));
        verify(visitorRepository, times(1)).findAll(Sort.by(ID_FIELD));
        assertThat(visitorDtoList).isNotNull().hasSize(initVisitorList.size());
        for (int i = 0; i < visitorDtoList.size(); i++) {
            assertThat(visitorDtoList.get(i).getId()).isEqualTo(initVisitorList.get(i).getId());
            assertThat(visitorDtoList.get(i).getAge()).isEqualTo(initVisitorList.get(i).getAge());
            assertThat(visitorDtoList.get(i).getEmail()).isEqualTo(initVisitorList.get(i).getEmail());
            assertThat(visitorDtoList.get(i).getInitials()).isEqualTo(initVisitorList.get(i).getInitials());
            assertThat(visitorDtoList.get(i).getDateOfBirth()).isEqualTo(initVisitorList.get(i).getDateOfBirth());
        }
    }

    @Test
    void updatedVisitorTest() {
        int idOfVisitorThatGonnaBeUpdated = 1;
        Visitor updatedVisitor = new Visitor().setInitials("William Jefferson Clinton");

        when(visitorRepository.findById(idOfVisitorThatGonnaBeUpdated)).thenReturn(Optional.of(updatedVisitor));

        visitorService.updatedVisitor(idOfVisitorThatGonnaBeUpdated, updatedVisitor);

        verify(visitorRepository, times(1)).save(updatedVisitor);
        assertThat(updatedVisitor.getId())
                .isEqualTo(1);
    }

    @Test
    void updatedVisitorDtoTest() {
        VisitorDto antonVisitorDto = new VisitorDto().setInitials("William Jefferson Clinton").setAge(33).setEmail("clinton@gmail.com")
                .setDateOfBirth(getDateBySpecificFormat(VISITOR_DATE_VALUES.getDateCreatingPattern(), "21-02-1995"));

        when(visitorRepository.findById(anyInt())).thenReturn(
                Optional.of(convertToEntity(antonVisitorDto, Visitor.class))
        );

        VisitorDto updatedVisitor = visitorService.updatedVisitor(STEVE_ID, antonVisitorDto);

        assertThat(updatedVisitor)
                .isNotNull()
                .hasFieldOrPropertyWithValue("initials", antonVisitorDto.getInitials())
                .hasFieldOrPropertyWithValue("age", antonVisitorDto.getAge())
                .hasFieldOrPropertyWithValue("email", antonVisitorDto.getEmail())
                .hasFieldOrPropertyWithValue("dateOfBirth", antonVisitorDto.getDateOfBirth());
    }

    @Test
    void deleteVisitorTest() {
        when(visitorRepository.findById(anyInt())).thenReturn(Optional.of(mock(Visitor.class)));
        visitorService.deleteVisitor(anyInt());
        verify(visitorRepository, times(1)).deleteById(anyInt());
    }

    @Test()
    void checkThatVisitorCantBeDeletedByNonExistedUser() {
        when(visitorRepository.findById(anyInt())).thenThrow(
                new EntityNotFoundException(VISITOR_BY_ID_NOT_FOUND.getErrorMessage())
        );

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> visitorService.deleteVisitor(anyInt())
        );

        verify(visitorRepository, never()).deleteById(anyInt());
        assertThat(exception.getMessage()).isEqualTo(VISITOR_BY_ID_NOT_FOUND.getErrorMessage());
    }

}