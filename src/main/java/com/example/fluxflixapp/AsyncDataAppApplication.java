package com.example.fluxflixapp;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.NoArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@SpringBootApplication
public class AsyncDataAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncDataAppApplication.class, args);
	}

	@Bean
	CommandLineRunner demo(DataRepository dataRepository) {
		return args -> {
			Stream.of("Name", "Card Owner", "Status", "State", "Last_Updated")
					.map(name -> new Data(UUID.randomUUID().toString(), name, name + "_Val"))
					.map(data -> dataRepository.save(data)).forEach(System.out::println);
		};
	}

}

@NoArgsConstructor
@ToString
class DataEvent {

	private Data data;

	public DataEvent(Data data, Date when, String user) {
		this.data = data;
		this.when = when;
		this.user = user;
	}

	public DataEvent() {
	}

	private Date when;
	private String user;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}

@Service
class DataService {

	private final DataRepository dataRepository;

	public DataService(DataRepository dataRepository) {
		this.dataRepository = dataRepository;
	}

	public Flux<DataEvent> streamStreams(Data data) {
		Flux.fromIterable(dataRepository.findAll());
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
		Flux<DataEvent> events = Flux.fromStream(Stream.generate(() -> new DataEvent(data, new Date(), randomUser())));
		return Flux.zip(interval, events).map(Tuple2::getT2);
	}

	public Flux<Data> all() {
		return Flux.fromIterable(dataRepository.findAll());
	}

	public Mono<Data> byId(String id) {
		Optional<Data> data = dataRepository.findById(id);
		return Mono.justOrEmpty(data);
	}

	private String randomUser() {
		String[] users = "arindam,debroop, mayukh,sourav,swastika".split(",");
		return users[new Random().nextInt(users.length)];
	}
}

@RestController
@RequestMapping("/data")
class DataEngineController {
	private final DataService dataService;

	public DataEngineController(DataService fluxFlixService) {
		this.dataService = fluxFlixService;
	}

	@GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<DataEvent> events(@PathVariable String id) {
		return dataService.byId(id).flatMapMany(dataService::streamStreams);
	}

	@GetMapping
	public Flux<Data> all() {
		return dataService.all();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Mono<Data> byId(@PathVariable String id) {
		System.out.println("Got the ID: " + id);
		return dataService.byId(id);
	}
}

@Repository
interface DataRepository extends CrudRepository<Data, String> {
}

@Entity
@NoArgsConstructor
@ToString
class Data {

	@Id
	private String id;
	private String dataKey;
	private String value;

	public Data() {
	}

	public Data(String id, String dataKey, String value) {
		this.id = id;
		this.dataKey = dataKey;
		this.value = value;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return dataKey;
	}

	public void setKey(String key) {
		this.dataKey = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String s = "Data: Key - " + getKey() + ", value - " + getValue();
		return s;
	}
}