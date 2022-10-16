/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guestbook;

import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The core class to bootstrap our application. It triggers Spring Boot's auto-configuration, component scanning and
 * configuration properties scanning using the {@link SpringBootApplication} convenience annotation. At the same time,
 * this class acts as configuration class to configure additional components (see {@link #init(GuestbookRepository)})
 * that the Spring container will take into account when bootstrapping.
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 */
@SpringBootApplication
public class Application {

	/**
	 * The main application method, bootstraps the Spring container.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Some initializing code to populate our database with some {@link GuestbookEntry}s. Beans of type
	 * {@link CommandLineRunner} will be executed on application startup which makes them a convenient way to run
	 * initialization code.
	 */
	@Bean
	CommandLineRunner init(GuestbookRepository guestbook) {

		return args -> {

			Stream.of( //
					new GuestbookEntry("H4xx0r", "first!!!","https://camo.githubusercontent.com/270d432a1df2cfd1ac26a6ad54689bb80ed6bd608b26d8b403aa1bc6ec93974e/68747470733a2f2f6834636b65722e6f72672f696d672f6834636b6572322e504e47"), //
					new GuestbookEntry("Arni", "Hasta la vista, baby","https://i.ebayimg.com/images/g/7koAAOSw8L5b9bE8/s-l400.jpg"), //
					new GuestbookEntry("Duke Nukem",
							"It's time to kick ass and chew bubble gum. And I'm all out of gum.","https://i0.wp.com/www.plauschangriff.de/wp-content/uploads/2018/07/dukenukem.jpg?fit=500%2C500&ssl=1"), //
					new GuestbookEntry("Gump1337",
							"Mama always said life was like a box of chocolates. You never know what you're gonna get.","https://img.freepik.com/free-vector/cute-astronaut-cat-lying-planet-animal-space_138676-2038.jpg?w=2000")) //
					.forEach(guestbook::save);
		};
	}

	/**
	 * This class customizes the web and web security configuration through callback methods provided by the
	 * {@link WebMvcConfigurer} interface.
	 */
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	static class SecurityConfiguration implements WebMvcConfigurer {

		/*
		 * (non-Javadoc)
		 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
		 */
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {

			// Route requests to /login to the login view (a default one provided by Spring Security)
			registry.addViewController("/login").setViewName("login");
		}

		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

			http.csrf().disable();

			// Allow all requests on the URI level, configure form login.
			http.authorizeRequests().anyRequest().permitAll() //
					.and().formLogin() //
					.and().logout().logoutSuccessUrl("/").clearAuthentication(true);

			return http.build();
		}
	}
}
