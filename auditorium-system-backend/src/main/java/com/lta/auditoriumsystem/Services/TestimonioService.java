package com.lta.auditoriumsystem.Services;

import com.lta.auditoriumsystem.Entities.Testimonio;

import java.util.List;

public interface TestimonioService {
    List<Testimonio> getAll();
    void saveTestimonio(Testimonio testimonio);
}
