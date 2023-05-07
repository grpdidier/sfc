package org.pe.lima.sg;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(SpringRunner.class)
@SpringBootTest(classes={SpringRunner.class})
public class SgApplicationTests {

	@Test
	public void contextLoads() {
	}

}
