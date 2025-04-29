package com.plasturgie.app.config;

import com.plasturgie.app.security.CustomUserDetailsService;
import com.plasturgie.app.security.JwtAuthenticationFilter;
import com.plasturgie.app.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
// Import AuthenticationConfiguration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Remove WebSecurityConfigurerAdapter import if you remove the extends clause
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Import SecurityFilterChain
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
// REMOVE "extends WebSecurityConfigurerAdapter" - Use component-based config instead
public class SecurityConfig /* extends WebSecurityConfigurerAdapter */ {

    // Keep Autowired fields
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // Keep this bean
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        // You might need to adjust constructor if it required AuthenticationManager before
        // Assuming it takes tokenProvider and userDetailsService as shown in your original code
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    // REMOVE the configure(AuthenticationManagerBuilder auth) method.
    // UserDetailsService and PasswordEncoder beans will be picked up automatically.
    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
    */

    // ** THIS IS THE FIX **
    // Define AuthenticationManager using AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // REMOVE the old authenticationManagerBean() override
    /*
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    */

    // Keep PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Keep CorsConfigurationSource bean (ensure it includes frontend origin)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // *** Make sure "http://localhost:4200" (or your frontend origin) is here! ***
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:5000",
                "http://0.0.0.0:5000",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // ** REPLACE configure(HttpSecurity http) with a SecurityFilterChain bean **
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource()).and() // Apply CORS
                .csrf().disable() // Disable CSRF for stateless APIs
                .exceptionHandling()
                    // Configure custom authentication entry point for 401 responses
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        // Provide a clearer error message if possible
                        response.getWriter().write(
                                "{\"error\": \"Unauthorized\", \"message\": \"Authentication required: " + authException.getMessage() + "\"}");
                    })
                .and()
                .authorizeRequests()
                    // Define public endpoints
                    .antMatchers("/api/auth/**", "/api/public/**", "/actuator/**" /*if using actuator*/).permitAll()
                    // Secure all other endpoints
                    .anyRequest().authenticated()
                .and()
                // Configure session management to be stateless
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // Add the custom JWT filter before the standard UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Build the SecurityFilterChain
    }
}