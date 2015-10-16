package ck.panda.web.resource;

import ck.panda.TestUtil;
import ck.panda.domain.entity.Department;
import ck.panda.config.TestContext;
import ck.panda.domian.entity.DepartmentBuilder;
import ck.panda.service.DepartmentService;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Krishna <krishnakumar@assistanz.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class})
//@ContextConfiguration(locations = {"classpath:testContext.xml", "classpath:exampleApplicationContext-web.xml"})
@WebAppConfiguration
public class DepartmentControllerTest {

    /**
     * Logger attribute.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentControllerTest.class);

    private MockMvc mockMvc;

    @Autowired
    private DepartmentService departmentServiceMock;

    @InjectMocks
    private DepartmentController departmentController;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        Mockito.reset(departmentServiceMock);

        this.mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
    }

    /**
     * Test of create method, of class DepartmentController.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
//        mockMvc.perform(post("/api/departments"))
//                .andExpect(status().isOk());
        Department department = new DepartmentBuilder()
                .name("Krishna")
                .description("Test creation")
                .build();

        Department added = new DepartmentBuilder()
                .id(1L)
                .description("Test creation")
                .name("Krishna")
                .build();

        when(departmentServiceMock.save(any(Department.class))).thenReturn(added);

        mockMvc.perform(post("/api/departments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(department)).header("Range", "1-10")
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test creation")))
                .andExpect(jsonPath("$.name", is("Krishna")));

        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentServiceMock, times(1)).save(departmentCaptor.capture());
        verifyNoMoreInteractions(departmentServiceMock);

        Department departmentArgument = departmentCaptor.getValue();
        assertNull(departmentArgument.getId());
        assertThat(departmentArgument.getDescription(), is("Test creation"));
        assertThat(departmentArgument.getName(), is("Krishna"));
    }

    /**
     * Test of read method, of class DepartmentController.
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        Department found = new DepartmentBuilder()
                .id(1L)
                .description("Test creation")
                .name("Krishna")
                .build();

        when(departmentServiceMock.find(1L)).thenReturn(found);

        mockMvc.perform(get("/api/departments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test creation")))
                .andExpect(jsonPath("$.name", is("Krishna")));

        verify(departmentServiceMock, times(1)).find(1L);
        verifyNoMoreInteractions(departmentServiceMock);
    }

    /**
     * Test of update method, of class DepartmentController.
     */
    @Test
    public void testUpdate() throws Exception {
        System.out.println("update");
        Department department = new DepartmentBuilder()
                .id(1L)
                .description("Test creation")
                .name("Krishna")
                .build();

        Department updated = new DepartmentBuilder()
                .id(1L)
                .description("Test creation")
                .name("Krishna")
                .build();

        when(departmentServiceMock.update(any(Department.class))).thenReturn(updated);

        mockMvc.perform(put("/api/departments/{id}", 1)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(department)).header("Range", "1-10")
        )
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test creation")))
                .andExpect(jsonPath("$.name", is("Krishna")));

        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentServiceMock, times(1)).update(departmentCaptor.capture());
        verifyNoMoreInteractions(departmentServiceMock);

        Department departmentArgument = departmentCaptor.getValue();
        assertThat(departmentArgument.getId(), is(1L));
        assertThat(departmentArgument.getDescription(), is("Test creation"));
        assertThat(departmentArgument.getName(), is("Krishna"));
    }

    /**
     * Test of update method, of class DepartmentController.
     */
    // Error due to delete method in service  is void. - Todo
//    @Test
//    public void testDelete() throws Exception {
//        System.out.println("delete");
//        Department deleted = new DepartmentBuilder()
//                .id(1L)
//                .description("Test creation")
//                .name("Krishna")
//                .build();
//
//        when(departmentServiceMock.delete(1L)).thenReturn(deleted); // Error due to delete method in service  is void. - Todo
//
//        mockMvc.perform(delete("/api/departments/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.description", is("Test creation")))
//                .andExpect(jsonPath("$.name", is("Krishna")));
//
//        verify(departmentServiceMock, times(1)).delete(1L);
//        verifyNoMoreInteractions(departmentServiceMock);
//    }

    /**
     * Test of list method, of class DepartmentController.
     */
    //Failed due to Pagenation Error in Department Controller - Todo
    @Test
    public void testList() throws Exception {
        System.out.println("list");
        //        mockMvc.perform(get("/api/departments").param("sortBy", "ASC").header("Range", "1-10"))
//                .andExpect(status().isOk());
//        Department first = new DepartmentBuilder()
//                .id(1L)
//                .description("Lorem ipsum")
//                .name("Foo")
//                .build();
//        Department second = new DepartmentBuilder()
//                .id(2L)
//                .description("Lorem ipsum")
//                .name("Bar")
//                .build();
//        Page<Department> Page = null;
//
//        when(departmentServiceMock.findAll(new PagingAndSorting("1-10", "ASC", Department.class))).thenReturn(Page);
//
//        mockMvc.perform(get("/api/departments").param("sortBy", "ASC").header("Range", "1-10"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[0].description", is("Lorem ipsum")))
//                .andExpect(jsonPath("$[0].name", is("Foo")))
//                .andExpect(jsonPath("$[1].id", is(2)))
//                .andExpect(jsonPath("$[1].description", is("Lorem ipsum")))
//                .andExpect(jsonPath("$[1].name", is("Bar")));
//
//        verify(departmentServiceMock, times(1)).findAll(new PagingAndSorting("1-10", "ASC", Department.class));
//        verifyNoMoreInteractions(departmentServiceMock);
        fail("Failed due to Pagenation Error in Department Controller");
    }

}
