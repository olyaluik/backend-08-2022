package ee.olga.webshop.repository;

import ee.olga.webshop.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {
    Person findPersonByEmail(String email);
}
