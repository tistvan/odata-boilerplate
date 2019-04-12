package com.penninkhof.odata;

import com.penninkhof.odata.entities.Member;
import com.penninkhof.odata.entities.Member2;
import com.penninkhof.odata.repository.Member2Repository;
import com.penninkhof.odata.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public CommandLineRunner demo(final MemberRepository repository, Member2Repository repository2) {
	    return new CommandLineRunner() {
			public void run(String... args) throws Exception {
				if (repository.count() == 0) {
					log.info("Database is still empty. Adding some sample records");
					repository.save(new Member(1, "Jack", "Bauer"));
					repository.save(new Member(2, "Chloe", "O'Brian"));
					repository.save(new Member(3, "Kim", "Bauer"));
					repository.save(new Member(4, "David", "Palmer"));
					repository.save(new Member(5, "Michelle", "Dessler"));
				}

				if (repository2.count() == 0) {
					log.info("Database is still empty. Adding some sample records asdf");
					repository2.save(new Member2(1, "Jack", "Bauer", "fdsa"));
					repository2.save(new Member2(2, "Chloe", "O'Brian", "dsfadf"));
					repository2.save(new Member2(3, "Kim", "Bauer", "sdafasdf"));
					repository2.save(new Member2(4, "David", "Palmer", "23rfwesdf"));
					repository2.save(new Member2(5, "Michelle", "Dessler", "f3q4tgdf"));
				}
	        }
	    };
	}
	
	

}