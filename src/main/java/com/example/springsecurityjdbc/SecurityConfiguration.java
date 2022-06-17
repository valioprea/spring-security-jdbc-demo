package com.example.springsecurityjdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void  configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication()
                .dataSource(dataSource) //this points to my h2 database for users and passwords. data source is h2
        //Code below is just an example. Not for production purposes!
//                .withDefaultSchema() //populates the dbs -> user table + authority table
//                .withUser(User.withUsername("user").password("pass").roles("USER"))
//                .withUser(User.withUsername("admin").password("admin").roles("ADMIN"));

        //the above code does by default what I created after commenting it with creating the schema and data
        // with the data.sql and schema.sql files under the resources area
                //and also, you can tell spring boot to configure it the way you want likeso:
                .usersByUsernameQuery("select username, password, enabled "+ "from users "+"where username = ?")
                .authoritiesByUsernameQuery("select username, authority "+"from authorities "+"where username = ?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/user").hasAnyRole("ADMIN", "USER")
                .antMatchers("/").permitAll()
                .and().formLogin();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
