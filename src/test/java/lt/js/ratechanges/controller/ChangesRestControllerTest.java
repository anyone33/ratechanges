package lt.js.ratechanges.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import lt.js.ratechanges.model.Change;
import lt.js.ratechanges.service.ChangesService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChangesRestControllerTest {

    private MockMvc mockMvc;
    
    private ChangesService mockService;
    
    private ChangesRestController controller;

    @Before
    public void setup() {
        mockService = mock(ChangesService.class);
        controller = new ChangesRestController(mockService);

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void testNoParameter() throws Exception {
        this.mockMvc.perform(get("/changes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidParameter() throws Exception {
        this.mockMvc.perform(get("/changes?forDate=aaaa"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSuccess() throws Exception {
        doReturn(Arrays.asList(new Change("AAA", new BigDecimal("1.1234"), ""))).when(mockService).getOrderedChanges(any());

        this.mockMvc.perform(get("/changes?forDate=2013-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].currency", is("AAA")))
                .andExpect(jsonPath("$.[0].changeRatio", is(1.1234)))
                .andExpect(jsonPath("$.[0].message", is("")));

        doReturn(Arrays.asList(new Change("AAA", new BigDecimal("1.0000"), ""), new Change("BBB", new BigDecimal("1.0000"), ""))).when(mockService).getOrderedChanges(any());
        this.mockMvc.perform(get("/changes?forDate=2013-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }
}
