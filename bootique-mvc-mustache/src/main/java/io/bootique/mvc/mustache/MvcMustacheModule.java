package io.bootique.mvc.mustache;

import com.google.inject.Binder;
import io.bootique.ConfigModule;
import io.bootique.mvc.MvcModule;

public class MvcMustacheModule extends ConfigModule {

	@Override
	public void configure(Binder binder) {
		MvcModule.contributeRenderers(binder).addBinding(".mustache").to(MustacheTemplateRenderer.class);
	}

}
