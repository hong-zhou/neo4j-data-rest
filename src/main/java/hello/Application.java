
package hello;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class Application {
	
	private final static Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	// populate neo4j database
	@Bean
	CommandLineRunner demo(PersonRepository personRepository) {
		return args -> {

			personRepository.deleteAll();

			Person greg = new Person("Greg", "Dean");
			Person roy = new Person("Roy", "Helen");
			Person craig = new Person("Craig", "Grace");

			List<Person> team = Arrays.asList(greg, roy, craig);

			log.info("Before linking up with Neo4j...");

			team.stream().forEach(person -> log.info("\t" + person.toString()));

			personRepository.save(greg);
			personRepository.save(roy);
			personRepository.save(craig);
			
			greg = personRepository.findByLastName(greg.getLastName());
			greg.worksWith(roy);
			greg.worksWith(craig);
			personRepository.save(greg);

			roy = personRepository.findByLastName(roy.getLastName());
			roy.worksWith(craig);
			// We already know that roy works with greg
			personRepository.save(roy);

			log.info("Lookup each person by name...");
			Iterable<Person> persons = personRepository.findAll();
			
			((Collection<Person>) persons).stream().forEach(person -> log.info(
					"\t" + personRepository.findByLastName(person.getLastName()).toString()));
		};
	}
}
