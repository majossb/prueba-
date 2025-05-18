package com.lta.auditoriumsystem.Services;

import com.lta.auditoriumsystem.Entities.Testimonio;
import com.lta.auditoriumsystem.Repositorios.TestimonioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestimonioServicelmpl implements TestimonioService {

    @Autowired
    private TestimonioRepository testimonioRepository;

    @Override
    public List<Testimonio> getAll() {
        return testimonioRepository.findAll();
    }

    @Override
    public void saveTestimonio(Testimonio testimonio) {
        testimonioRepository.save(testimonio);
    }
}
