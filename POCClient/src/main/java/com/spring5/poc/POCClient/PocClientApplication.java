package com.spring5.poc.POCClient;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class PocClientApplication {

	@Bean
	WebClient webClient() {
		return WebClient.create("http://localhost:8080/data");

	}

	@Bean
	CommandLineRunner demo(WebClient client) {
		return strings -> client.get().uri("").retrieve().bodyToFlux(Data.class).filter(data -> data.getKey() != null)
				.flatMap(data -> client.get().uri("/{id}/events", data.getId()).retrieve().bodyToFlux(DataEvent.class))
				.subscribe(dataEvent -> System.out.println(dataEvent.toString()));
	};

	public static void main(String[] args) {
		SpringApplication.run(PocClientApplication.class, args);
	}

}

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

	@Override
	public String toString() {
		return "DataEvent [data=" + data + ", when=" + when + ", user=" + user + "]";
	}

}

class Data {

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