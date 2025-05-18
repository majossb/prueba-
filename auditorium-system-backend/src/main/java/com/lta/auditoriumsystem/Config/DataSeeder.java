package com.lta.auditoriumsystem.Config;

import com.lta.auditoriumsystem.Entities.Testimonio;
import com.lta.auditoriumsystem.Repositorios.TestimonioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initTestimonios(TestimonioRepository repository) {
        return args -> {
            if (repository.count() == 0) {

                repository.save(new Testimonio(
                        null,
                        "Emanuel Moreno",
                        "Organizador de Eventos, Universidad Surcolombiana",
                        "SpotReserve facilitó la gestión de nuestras reservas de auditorios, ahorrándonos tiempo y esfuerzo. ¡Muy recomendable!",
                        "/images/personatestimonio1.jpg"
                ));

                repository.save(new Testimonio(
                        null,
                        "Santiago Ortiz",
                        "Director del Yumana",
                        "La plataforma es muy intuitiva, logramos coordinar actividades sin errores ni confusiones.",
                        "/images/testimonio2.jpg"
                ));

                repository.save(new Testimonio(
                        null,
                        "Laura Pérez",
                        "Coordinadora Académica, Corhuila",
                        "Gracias a SpotReserve mejoramos la gestión de reservas para eventos académicos de manera eficaz.",
                        "/images/chicatestimonio.jpg"
                ));
            }
        };
    }
}
