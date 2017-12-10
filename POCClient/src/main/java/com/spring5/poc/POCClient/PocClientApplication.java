package com.spring5.poc.POCClient;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This is a Client which also exposes a RestService which is supposed to be
 * consumed by the other RestService
 * 
 * @author swastika
 *
 */

@SpringBootApplication
@RestController
public class PocClientApplication {

	@Autowired
	WebClient adapterClient;

	@Bean
	WebClient webClient() {
		return WebClient.create("http://localhost:8080/Spring5Reactive/data");

	}

	// @Bean
	CommandLineRunner demo(WebClient client) {
		return strings -> client.get().uri("").retrieve().bodyToFlux(Data.class).filter(data -> data.getKey() != null)
				.flatMap(data -> client.get().uri("/{id}/events", data.getId()).retrieve().bodyToFlux(DataEvent.class))
				.subscribe(dataEvent -> System.out.println(dataEvent.toString()));
	};

	/**
	 * This is a dummy start method, which is supposed to make a call Adapter
	 * and get a dummy acknowledgement
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "start", method = RequestMethod.GET)
	public String getData(@RequestParam List<String> input) throws Exception {

		RestTemplate restTemplate = new RestTemplate();

		System.out.println("Received request in GetData");
		Date startTime = new java.util.Date();
		String adapterURL = "http://localhost:8080/asyncdata?input=" + input;
		String ack = restTemplate.getForObject(adapterURL, String.class);
		Date endTime = new java.util.Date();
		System.out.println("In GetData Getting acknowledgement " + ack + " from Adapter in "
				+ (endTime.getTime() - startTime.getTime()));
		return ack;
	}

	/**
	 * This is a method which is supposed to be called by Adapter Client to
	 * provide the data details, this should be a post call,for the sake of POC
	 * keeping it GET call
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "notify", method = RequestMethod.GET)
	public String receiveNotification(@RequestParam String input) throws Exception {
		System.out.println("Received Notification in Notify Method Input " + input);
		return "Thanks";
	}

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