package ml.echelon133.sportevents.stadium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.exception.APIExceptionHandler;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@RunWith(MockitoJUnitRunner.class)
public class StadiumControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StadiumService stadiumService;

    @Mock
    private StadiumResourceAssembler resourceAssembler;

    @InjectMocks
    private StadiumController stadiumController;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    private JacksonTester<StadiumDto> jsonStadiumDto;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mockMvc = MockMvcBuilders.standaloneSetup(stadiumController).setControllerAdvice(exceptionHandler).build();

        // We always use actual toResource/toResources implementation
        given(resourceAssembler.toResource(any(Stadium.class))).willCallRealMethod();
        given(resourceAssembler.toResources(anyIterable())).willCallRealMethod();
    }

    @Test
    public void getStadiumsReturnsEmptyResourcesCorrectly() throws Exception {
        // Given
        given(stadiumService.findAll()).willReturn(Collections.emptyList());

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/stadiums")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext responseJson = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseJson.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(responseJson.read("$.content").toString()).isEqualTo("[]");
    }

    @Test
    public void getStadiumsReturnsExistingResourcesCorrectly() throws Exception {
        Stadium stadium = new Stadium("Test stadium", "Test city", 40000);
        stadium.setId(1L);

        // Given
        given(stadiumService.findAll()).willReturn(Collections.singletonList(stadium));

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/stadiums")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(json.read("$.content[0].id").toString()).isEqualTo(stadium.getId().toString());
        assertThat(json.read("$.content[0].name").toString()).isEqualTo(stadium.getName());
        assertThat(json.read("$.content[0].city").toString()).isEqualTo(stadium.getCity());
        assertThat(json.read("$.content[0].capacity").toString()).isEqualTo(stadium.getCapacity().toString());
        assertThat(json.read("$.content[0].links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(json.read("$.content[0].links[?(@.rel=='self')].href").toString())
                .contains("/api\\/stadiums\\/" + stadium.getId().toString());
    }

    @Test
    public void getStadiumReturnsCorrectResponseWhenResourceDoesNotExist() throws Exception {
        // Given
        given(stadiumService.findById(1L)).willThrow(new ResourceDoesNotExistException("Stadium with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/stadiums/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Stadium with this id does not exist");
    }

    @Test
    public void getStadiumReturnsExistingResourceCorrectly() throws Exception {
        Stadium stadium = new Stadium("Test stadium", "Test city", 40000);
        stadium.setId(1L);

        // Given
        given(stadiumService.findById(1L)).willReturn(stadium);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/stadiums/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString()).contains("/api\\/stadiums\\/" + stadium.getId());
        assertThat(json.read("$.id").toString()).isEqualTo(stadium.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(stadium.getName());
        assertThat(json.read("$.city").toString()).isEqualTo(stadium.getCity());
        assertThat(json.read("$.capacity").toString()).isEqualTo(stadium.getCapacity().toString());
    }

    @Test
    public void createStadiumStadiumDtoNullFieldsAreValidated() throws Exception {
        StadiumDto stadiumDto = new StadiumDto(null, null, 40000);

        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/stadiums")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("city validation error: must not be null");
        assertThat(response.getContentAsString()).contains("name validation error: must not be null");
    }

    @Test
    public void createStadiumStadiumDtoPositiveFieldsAreValidated() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("name", "city", -1);

        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/stadiums")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("capacity validation error: must be greater than 0");
    }

    @Test
    public void createStadiumStadiumDtoFieldLengthsAreValidated() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("n", "c", null);

        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/stadiums")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("city validation error: length must be between 2 and 150");
        assertThat(response.getContentAsString()).contains("name validation error: length must be between 3 and 200");
    }

    @Test
    public void createStadiumReturnsCorrectResponseWhenStadiumDtoPassesValidation() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("name", "city", 2000);
        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // "Saved" stadium
        Stadium stadium = new Stadium(stadiumDto.getName(), stadiumDto.getCity(), stadiumDto.getCapacity());
        stadium.setId(1L);

        // Given
        given(stadiumService.convertDtoToEntity(any(StadiumDto.class))).willReturn(stadium);
        given(stadiumService.save(any(Stadium.class))).willReturn(stadium);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/stadiums")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(json.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString()).contains("/api\\/stadiums\\/" + stadium.getId());
        assertThat(json.read("$.id").toString()).isEqualTo(stadium.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(stadium.getName());
        assertThat(json.read("$.city").toString()).isEqualTo(stadium.getCity());
        assertThat(json.read("$.capacity").toString()).isEqualTo(stadium.getCapacity().toString());
    }

    @Test
    public void replaceStadiumStadiumDtoNullFieldsAreValidated() throws Exception {
        StadiumDto stadiumDto = new StadiumDto(null, null, 20000);

        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/stadiums/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("city validation error: must not be null");
        assertThat(response.getContentAsString()).contains("name validation error: must not be null");
    }

    @Test
    public void replaceStadiumStadiumDtoFieldLengthsAreValidated() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("n", "c", null);

        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/stadiums/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("city validation error: length must be between 2 and 150");
        assertThat(response.getContentAsString()).contains("name validation error: length must be between 3 and 200");
    }

    @Test
    public void replaceStadiumStadiumDtoPositiveFieldsAreValidated() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("name", "city", -1);

        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/stadiums/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("capacity validation error: must be greater than 0");
    }

    @Test
    public void replaceStadiumReturnsCorrectResponseWhenResourceDoesNotExist() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("name", "city", 40000);

        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // Given
        given(stadiumService.findById(1L)).willThrow(new ResourceDoesNotExistException("Stadium with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/stadiums/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Stadium with this id does not exist");
    }

    @Test
    public void replaceStadiumReturnsCorrectResponseOnSuccessfulReplacement() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("name", "city", 40000);
        JsonContent<StadiumDto> stadiumDtoJsonContent = jsonStadiumDto.write(stadiumDto);

        // "Saved" stadium
        Stadium stadium = new Stadium(stadiumDto.getName(), stadiumDto.getCity(), stadiumDto.getCapacity());
        stadium.setId(1L);

        // Given
        given(stadiumService.findById(1L)).willReturn(stadium);
        given(stadiumService.convertDtoToEntity(any(StadiumDto.class))).willReturn(stadium);
        given(stadiumService.save(stadium)).willReturn(stadium);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/stadiums/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stadiumDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString()).contains("/api\\/stadiums\\/" + stadium.getId());
        assertThat(json.read("$.id").toString()).isEqualTo(stadium.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(stadium.getName());
        assertThat(json.read("$.city").toString()).isEqualTo(stadium.getCity());
        assertThat(json.read("$.capacity").toString()).isEqualTo(stadium.getCapacity().toString());
    }

    @Test
    public void deleteStadiumReturnsCorrectResponseAfterDeletingResource() throws Exception {
        // Given
        given(stadiumService.deleteById(any())).willReturn(true);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                delete("/api/stadiums/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"deleted\":true}");
    }
}
