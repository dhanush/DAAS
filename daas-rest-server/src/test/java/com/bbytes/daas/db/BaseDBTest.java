
package com.bbytes.daas.db;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bbytes.daas.rest.DAASTesting;

/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/rest-daas-server-context.xml" })
public class BaseDBTest extends DAASTesting{

}
