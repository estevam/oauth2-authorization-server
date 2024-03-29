/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.est.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import ca.est.federation.FederatedIdentityAuthenticationSuccessHandler;
import ca.est.util.ConstantUtil;
/**
 * @author Estevam Meneses
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.cors(CorsConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize.requestMatchers(ConstantUtil.WHITE_LIST).permitAll());

		http.authorizeHttpRequests(
				auth -> auth.requestMatchers(ConstantUtil.WHITE_LIST).permitAll().anyRequest().authenticated())
				.formLogin(formLogin -> formLogin.loginPage(ConstantUtil.LOGIN)).oauth2Login(
						oauth2Login -> oauth2Login.loginPage(ConstantUtil.LOGIN).successHandler(authenticationSuccessHandler()));

		return http.build();
	}

	AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new FederatedIdentityAuthenticationSuccessHandler();
	}

	@Bean
	UserDetailsService users() {
		@SuppressWarnings("deprecation")
		UserDetails user = User.withDefaultPasswordEncoder().username("user1").password("password").roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

}
