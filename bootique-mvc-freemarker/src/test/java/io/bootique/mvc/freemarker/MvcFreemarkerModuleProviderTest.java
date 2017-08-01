package io.bootique.mvc.freemarker;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

/**
 * @author Lukasz Bachman
 */
public class MvcFreemarkerModuleProviderTest {

	@Test
	public void testPresentInJar() {
		BQModuleProviderChecker.testPresentInJar(MvcFreemarkerModuleProvider.class);
	}
}