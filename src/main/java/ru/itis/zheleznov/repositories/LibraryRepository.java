package ru.itis.zheleznov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.zheleznov.entities.Library;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

}
