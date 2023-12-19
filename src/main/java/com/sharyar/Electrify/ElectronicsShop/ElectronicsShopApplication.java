package com.sharyar.Electrify.ElectronicsShop;

import com.sharyar.Electrify.ElectronicsShop.entities.Role;
import com.sharyar.Electrify.ElectronicsShop.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@ComponentScan({"com.sharyar.Electrify.ElectronicsShop" , "com.sharyar.Electrify.ElectronicsShop.dto"  })
public class ElectronicsShopApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicsShopApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;
    @Autowired
	private RoleRepository roleRepository;
	@Value("${role.user.id}")
	private String userRoleId;
	@Value(("${role.admin.id}"))
	private String adminRoleId;

	@Override
	public void run(String... args) throws Exception {
//		System.out.println(passwordEncoder.encode("audionic123&"));

			Role user = Role.builder().roleId(userRoleId).roleName("ROLE_USER").build();
			Role admin = Role.builder().roleId(adminRoleId).roleName("ROLE_ADMIN").build();

			roleRepository.save(user);
			roleRepository.save(admin);

	}
}
