package ru.itis.zheleznov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.zheleznov.entities.PublisherInformation;

public interface PublisherInformationRepository extends JpaRepository<PublisherInformation, Long> {
}
