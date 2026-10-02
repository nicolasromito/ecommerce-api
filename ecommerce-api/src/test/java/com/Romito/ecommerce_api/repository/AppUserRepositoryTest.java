package com.Romito.ecommerce_api.repository;

import com.Romito.ecommerce_api.model.AppUser;
import com.Romito.ecommerce_api.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void lasFilasDeRoleSeCarganPorLaMigracionSeed() {
        Optional<Role> admin = roleRepository.findByName("ADMIN");
        Optional<Role> user = roleRepository.findByName("USER");

        assertTrue(admin.isPresent());
        assertTrue(user.isPresent());
    }

    @Test
    void guardaUsuarioYLoEncuentraPorUsername() {
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        AppUser appUser = new AppUser();
        appUser.setUsername("jromito");
        appUser.setEmail("jromito@example.com");
        appUser.setPassword("hash-ficticio");
        appUser.setRole(userRole);
        appUserRepository.save(appUser);

        em.flush();
        em.clear();

        Optional<AppUser> found = appUserRepository.findByUsername("jromito");

        assertTrue(found.isPresent());
        assertEquals("jromito@example.com", found.get().getEmail());
        assertEquals("USER", found.get().getRole().getName());
    }

    @Test
    void existsByUsernameYExistsByEmailFuncionanComoSeEspera() {
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        AppUser appUser = new AppUser();
        appUser.setUsername("mperez");
        appUser.setEmail("mperez@example.com");
        appUser.setPassword("hash-ficticio");
        appUser.setRole(userRole);
        appUserRepository.saveAndFlush(appUser);

        assertTrue(appUserRepository.existsByUsername("mperez"));
        assertTrue(appUserRepository.existsByEmail("mperez@example.com"));
        assertFalse(appUserRepository.existsByUsername("noexiste"));
    }
}