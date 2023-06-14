package ru.itis.zheleznov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.zheleznov.entities.LibraryNode;

@Repository
public interface LibraryNodeRepository extends JpaRepository<LibraryNode, Long> {
}
