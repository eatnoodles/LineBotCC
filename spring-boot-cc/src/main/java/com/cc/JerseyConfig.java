package com.cc;



import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.cc.ws.NudoControl;


/**
 * @author Caleb Cheng
 *
 */
@Configuration
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(NudoControl.class);
	}

}